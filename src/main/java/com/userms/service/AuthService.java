package com.userms.service;

import com.userms.common.ErrorCodes;
import com.userms.dto.AuthResponseDTO;
import com.userms.dto.UserDTO;
import com.userms.dto.UserLoginDTO;
import com.userms.dto.UserRegisterDTO;
import com.userms.entity.Role;
import com.userms.entity.User;
import com.userms.exception.BusinessException;
import com.userms.repository.UserRepository;
import com.userms.repository.UserRoleRepository;
import com.userms.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserDTO register(UserRegisterDTO registerDTO) {
        log.info("開始註冊用戶: {}", registerDTO.getUsername());

        validateRegisterData(registerDTO);

        User user = createUser(registerDTO);
        User savedUser = userRepository.save(user);

        log.info("用戶註冊成功: {}", savedUser.getUsername());
        return convertToUserDTO(savedUser);
    }

    public AuthResponseDTO login(UserLoginDTO loginDTO) {
        log.info("用戶嘗試登入: {}", loginDTO.getUsernameOrEmail());

        User user = findUserByUsernameOrEmail(loginDTO.getUsernameOrEmail());

        if (!user.isActive()) {
            throw new BusinessException("帳戶已被停用", ErrorCodes.USER_INACTIVE);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("用戶名或密碼錯誤", ErrorCodes.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        UserDTO userDTO = convertToUserDTO(user);

        log.info("用戶登入成功: {}", user.getUsername());
        return new AuthResponseDTO(token, userDTO);
    }

    public UserDTO getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用戶不存在", ErrorCodes.USER_NOT_FOUND));
        return convertToUserDTO(user);
    }

    public AuthResponseDTO refreshToken(String oldToken) {
        log.info("嘗試刷新 Token");
        
        // 檢查 Token 是否可以刷新
        if (!jwtUtil.canTokenBeRefreshed(oldToken)) {
            throw new BusinessException("Token 無法刷新，請重新登入", ErrorCodes.TOKEN_EXPIRED);
        }
        
        try {
            // 從舊 Token 獲取用戶名
            String username = jwtUtil.getUsernameFromToken(oldToken);
            
            // 驗證用戶是否仍然存在且啟用
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("用戶不存在", ErrorCodes.USER_NOT_FOUND));
            
            if (!user.isActive()) {
                throw new BusinessException("帳戶已被停用", ErrorCodes.USER_INACTIVE);
            }
            
            // 生成新的 Token
            String newToken = jwtUtil.refreshToken(oldToken);
            UserDTO userDTO = convertToUserDTO(user);
            
            log.info("Token 刷新成功: {}", username);
            return new AuthResponseDTO(newToken, userDTO);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token 刷新過程發生錯誤: {}", e.getMessage());
            throw new BusinessException("Token 刷新失敗", ErrorCodes.INVALID_TOKEN);
        }
    }

    private void validateRegisterData(UserRegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException("使用者名稱已存在", ErrorCodes.USERNAME_EXISTS);
        }

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException("電子郵件已被使用", ErrorCodes.EMAIL_EXISTS);
        }

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("密碼與確認密碼不一致", ErrorCodes.PASSWORD_MISMATCH);
        }
    }

    private User createUser(UserRegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setIsActive(true);

        // 手動設置時間戳以確保不為空
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        long timestamp = System.currentTimeMillis();
        user.setCreatedTime(now);
        user.setCreatedTs(timestamp);
        user.setUpdatedTime(now);
        user.setUpdatedTs(timestamp);

        return user;
    }

    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new BusinessException("用戶名或密碼錯誤", ErrorCodes.INVALID_CREDENTIALS));
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedTime(user.getCreatedTime());
        dto.setUpdatedTime(user.getUpdatedTime());

        List<Role> roles = userRoleRepository.findRolesByUserIdAndIsActive(user.getId(), true);
        dto.setRoles(roles.stream().map(Role::getRoleCode).toList());

        return dto;
    }
}