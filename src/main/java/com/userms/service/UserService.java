package com.userms.service;

import com.userms.common.ErrorCodes;
import com.userms.common.PageResult;
import com.userms.dto.UserDTO;
import com.userms.dto.UserRegisterDTO;
import com.userms.entity.Role;
import com.userms.entity.User;
import com.userms.entity.UserRole;
import com.userms.exception.BusinessException;
import com.userms.repository.RoleRepository;
import com.userms.repository.UserRepository;
import com.userms.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public PageResult<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::convertToUserDTO)
                .toList();
        
        return PageResult.of(userDTOs, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }

    public UserDTO getUserById(String userId) {
        User user = findUserById(userId);
        return convertToUserDTO(user);
    }

    public List<UserDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
                keyword, keyword, keyword, keyword);
        return users.stream().map(this::convertToUserDTO).toList();
    }

    @Transactional
    public UserDTO createUser(UserRegisterDTO registerDTO) {
        log.info("創建用戶: {}", registerDTO.getUsername());

        validateCreateUser(registerDTO);

        User user = createUserFromDTO(registerDTO);
        User savedUser = userRepository.save(user);

        log.info("用戶創建成功: {}", savedUser.getUsername());
        return convertToUserDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(String userId, UserDTO userDTO) {
        log.info("更新用戶: {}", userId);

        User existingUser = findUserById(userId);
        
        // 檢查用戶名和電子郵件唯一性（排除當前用戶）
        if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("使用者名稱已存在", ErrorCodes.USERNAME_EXISTS);
        }

        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("電子郵件已被使用", ErrorCodes.EMAIL_EXISTS);
        }

        // 更新用戶資訊
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setIsActive(userDTO.getIsActive());
        existingUser.setUpdatedTime(LocalDateTime.now());
        existingUser.setUpdatedTs(System.currentTimeMillis());

        User savedUser = userRepository.save(existingUser);
        log.info("用戶更新成功: {}", savedUser.getUsername());
        
        return convertToUserDTO(savedUser);
    }

    @Transactional
    public void enableUser(String userId) {
        log.info("啟用用戶: {}", userId);
        User user = findUserById(userId);
        user.setIsActive(true);
        user.setUpdatedTime(LocalDateTime.now());
        user.setUpdatedTs(System.currentTimeMillis());
        userRepository.save(user);
        log.info("用戶啟用成功: {}", user.getUsername());
    }

    @Transactional
    public void disableUser(String userId) {
        log.info("停用用戶: {}", userId);
        User user = findUserById(userId);
        user.setIsActive(false);
        user.setUpdatedTime(LocalDateTime.now());
        user.setUpdatedTs(System.currentTimeMillis());
        userRepository.save(user);
        log.info("用戶停用成功: {}", user.getUsername());
    }

    @Transactional
    public void deleteUser(String userId) {
        log.info("刪除用戶: {}", userId);
        User user = findUserById(userId);
        
        // 刪除用戶角色關聯
        userRoleRepository.deleteAllByUserId(userId);
        
        // 刪除用戶
        userRepository.delete(user);
        log.info("用戶刪除成功: {}", user.getUsername());
    }

    @Transactional
    public void assignRole(String userId, String roleCode) {
        log.info("為用戶 {} 分配角色 {}", userId, roleCode);
        
        User user = findUserById(userId);
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new BusinessException("角色不存在", ErrorCodes.ROLE_NOT_FOUND));

        // 檢查是否已經分配該角色
        if (userRoleRepository.hasRole(userId, roleCode)) {
            throw new BusinessException("用戶已擁有該角色", ErrorCodes.ROLE_ALREADY_ASSIGNED);
        }

        UserRole userRole = new UserRole(user, role);

        userRoleRepository.save(userRole);
        log.info("角色分配成功: 用戶 {} 獲得角色 {}", user.getUsername(), role.getRoleName());
    }

    @Transactional
    public void revokeRole(String userId, String roleCode) {
        log.info("撤銷用戶 {} 的角色 {}", userId, roleCode);
        
        User user = findUserById(userId);
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new BusinessException("角色不存在", ErrorCodes.ROLE_NOT_FOUND));

        // 檢查用戶是否擁有該角色
        if (!userRoleRepository.hasRole(userId, roleCode)) {
            throw new BusinessException("用戶未擁有該角色", ErrorCodes.ROLE_NOT_ASSIGNED);
        }

        // 直接刪除用戶角色關聯
        UserRole.UserRoleId userRoleId = new UserRole.UserRoleId(userId, role.getId());
        userRoleRepository.deleteById(userRoleId);
        log.info("角色撤銷成功: 用戶 {} 失去角色 {}", user.getUsername(), role.getRoleName());
    }

    public List<String> getUserRoles(String userId) {
        findUserById(userId); // 驗證用戶存在
        return userRoleRepository.findRoleCodesByUserId(userId);
    }

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用戶不存在", ErrorCodes.USER_NOT_FOUND));
    }

    private void validateCreateUser(UserRegisterDTO registerDTO) {
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

    private User createUserFromDTO(UserRegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setIsActive(true);

        LocalDateTime now = LocalDateTime.now();
        long timestamp = System.currentTimeMillis();
        user.setCreatedTime(now);
        user.setCreatedTs(timestamp);
        user.setUpdatedTime(now);
        user.setUpdatedTs(timestamp);

        return user;
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

        dto.setRoles(userRoleRepository.findRoleCodesByUserId(user.getId()));

        return dto;
    }
}