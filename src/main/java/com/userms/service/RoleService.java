package com.userms.service;

import com.userms.common.ErrorCodes;
import com.userms.common.PageResult;
import com.userms.dto.RoleCreateDTO;
import com.userms.dto.RoleDTO;
import com.userms.entity.Permission;
import com.userms.entity.Role;
import com.userms.entity.RolePermission;
import com.userms.exception.BusinessException;
import com.userms.repository.PermissionRepository;
import com.userms.repository.RolePermissionRepository;
import com.userms.repository.RoleRepository;
import com.userms.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    public PageResult<RoleDTO> getAllRoles(Pageable pageable) {
        Page<Role> rolePage = roleRepository.findAll(pageable);
        List<RoleDTO> roleDTOs = rolePage.getContent().stream()
                .map(this::convertToRoleDTO)
                .toList();
        
        return PageResult.of(roleDTOs, rolePage.getNumber(), rolePage.getSize(), rolePage.getTotalElements());
    }

    public PageResult<RoleDTO> getActiveRoles(Pageable pageable) {
        Page<Role> rolePage = roleRepository.findByIsActive(true, pageable);
        List<RoleDTO> roleDTOs = rolePage.getContent().stream()
                .map(this::convertToRoleDTO)
                .toList();
        
        return PageResult.of(roleDTOs, rolePage.getNumber(), rolePage.getSize(), rolePage.getTotalElements());
    }

    public RoleDTO getRoleById(String roleId) {
        Role role = findRoleById(roleId);
        return convertToRoleDTO(role);
    }

    public RoleDTO getRoleByCode(String roleCode) {
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new BusinessException("角色不存在", ErrorCodes.ROLE_NOT_FOUND));
        return convertToRoleDTO(role);
    }

    public List<RoleDTO> searchRoles(String keyword) {
        Page<Role> rolePage = roleRepository.findByKeyword(keyword, Pageable.unpaged());
        return rolePage.getContent().stream()
                .map(this::convertToRoleDTO)
                .toList();
    }

    @Transactional
    public RoleDTO createRole(RoleCreateDTO createDTO) {
        log.info("創建角色: {}", createDTO.getRoleCode());

        validateCreateRole(createDTO);

        Role role = createRoleFromDTO(createDTO);
        Role savedRole = roleRepository.save(role);

        log.info("角色創建成功: {}", savedRole.getRoleCode());
        return convertToRoleDTO(savedRole);
    }

    @Transactional
    public RoleDTO updateRole(String roleId, RoleDTO roleDTO) {
        log.info("更新角色: {}", roleId);

        Role existingRole = findRoleById(roleId);
        
        // 檢查角色名稱和代碼唯一性（排除當前角色）
        if (!existingRole.getRoleName().equals(roleDTO.getRoleName()) &&
            roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new BusinessException("角色名稱已存在", ErrorCodes.ROLE_NOT_FOUND);
        }

        if (!existingRole.getRoleCode().equals(roleDTO.getRoleCode()) &&
            roleRepository.existsByRoleCode(roleDTO.getRoleCode())) {
            throw new BusinessException("角色代碼已存在", ErrorCodes.ROLE_NOT_FOUND);
        }

        // 更新角色資訊
        existingRole.setRoleName(roleDTO.getRoleName());
        existingRole.setRoleCode(roleDTO.getRoleCode());
        existingRole.setDescription(roleDTO.getDescription());
        existingRole.setIsActive(roleDTO.getIsActive());
        existingRole.setUpdatedTime(LocalDateTime.now());
        existingRole.setUpdatedTs(System.currentTimeMillis());

        Role savedRole = roleRepository.save(existingRole);
        log.info("角色更新成功: {}", savedRole.getRoleCode());
        
        return convertToRoleDTO(savedRole);
    }

    @Transactional
    public void enableRole(String roleId) {
        log.info("啟用角色: {}", roleId);
        Role role = findRoleById(roleId);
        role.setIsActive(true);
        role.setUpdatedTime(LocalDateTime.now());
        role.setUpdatedTs(System.currentTimeMillis());
        roleRepository.save(role);
        log.info("角色啟用成功: {}", role.getRoleCode());
    }

    @Transactional
    public void disableRole(String roleId) {
        log.info("停用角色: {}", roleId);
        Role role = findRoleById(roleId);
        role.setIsActive(false);
        role.setUpdatedTime(LocalDateTime.now());
        role.setUpdatedTs(System.currentTimeMillis());
        roleRepository.save(role);
        log.info("角色停用成功: {}", role.getRoleCode());
    }

    @Transactional
    public void deleteRole(String roleId) {
        log.info("刪除角色: {}", roleId);
        Role role = findRoleById(roleId);
        
        // 檢查是否有用戶使用此角色
        long userCount = userRoleRepository.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new BusinessException("無法刪除角色，還有用戶正在使用此角色", ErrorCodes.ROLE_NOT_FOUND);
        }
        
        // 刪除角色權限關聯
        rolePermissionRepository.deleteAllByRoleId(roleId);
        
        // 刪除角色
        roleRepository.delete(role);
        log.info("角色刪除成功: {}", role.getRoleCode());
    }

    @Transactional
    public void assignPermission(String roleId, String permissionCode) {
        log.info("為角色 {} 分配權限 {}", roleId, permissionCode);
        
        Role role = findRoleById(roleId);
        Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                .orElseThrow(() -> new BusinessException("權限不存在", ErrorCodes.PERMISSION_NOT_FOUND));

        // 檢查是否已經分配該權限
        if (rolePermissionRepository.hasPermission(roleId, permissionCode)) {
            throw new BusinessException("角色已擁有該權限", ErrorCodes.ROLE_ALREADY_ASSIGNED);
        }

        RolePermission rolePermission = new RolePermission(role, permission);
        rolePermissionRepository.save(rolePermission);
        log.info("權限分配成功: 角色 {} 獲得權限 {}", role.getRoleCode(), permission.getPermissionName());
    }

    @Transactional
    public void revokePermission(String roleId, String permissionCode) {
        log.info("撤銷角色 {} 的權限 {}", roleId, permissionCode);
        
        Role role = findRoleById(roleId);
        Permission permission = permissionRepository.findByPermissionCode(permissionCode)
                .orElseThrow(() -> new BusinessException("權限不存在", ErrorCodes.PERMISSION_NOT_FOUND));

        // 檢查角色是否擁有該權限
        if (!rolePermissionRepository.hasPermission(roleId, permissionCode)) {
            throw new BusinessException("角色未擁有該權限", ErrorCodes.ROLE_NOT_ASSIGNED);
        }

        // 直接刪除角色權限關聯
        RolePermission.RolePermissionId rolePermissionId = new RolePermission.RolePermissionId(roleId, permission.getId());
        rolePermissionRepository.deleteById(rolePermissionId);
        log.info("權限撤銷成功: 角色 {} 失去權限 {}", role.getRoleCode(), permission.getPermissionName());
    }

    public List<String> getRolePermissions(String roleId) {
        findRoleById(roleId); // 驗證角色存在
        return rolePermissionRepository.findPermissionCodesByRoleId(roleId);
    }

    public long getUserCountByRole(String roleId) {
        findRoleById(roleId); // 驗證角色存在
        return userRoleRepository.countActiveUsersByRoleId(roleId);
    }

    private Role findRoleById(String roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在", ErrorCodes.ROLE_NOT_FOUND));
    }

    private void validateCreateRole(RoleCreateDTO createDTO) {
        if (roleRepository.existsByRoleName(createDTO.getRoleName())) {
            throw new BusinessException("角色名稱已存在", ErrorCodes.ROLE_NOT_FOUND);
        }

        if (roleRepository.existsByRoleCode(createDTO.getRoleCode())) {
            throw new BusinessException("角色代碼已存在", ErrorCodes.ROLE_NOT_FOUND);
        }
    }

    private Role createRoleFromDTO(RoleCreateDTO createDTO) {
        Role role = new Role();
        role.setRoleName(createDTO.getRoleName());
        role.setRoleCode(createDTO.getRoleCode());
        role.setDescription(createDTO.getDescription());
        role.setIsActive(true);

        LocalDateTime now = LocalDateTime.now();
        long timestamp = System.currentTimeMillis();
        role.setCreatedTime(now);
        role.setCreatedTs(timestamp);
        role.setUpdatedTime(now);
        role.setUpdatedTs(timestamp);

        return role;
    }

    private RoleDTO convertToRoleDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleCode(role.getRoleCode());
        dto.setDescription(role.getDescription());
        dto.setIsActive(role.getIsActive());
        dto.setCreatedTime(role.getCreatedTime());
        dto.setUpdatedTime(role.getUpdatedTime());

        // 獲取權限列表
        dto.setPermissions(rolePermissionRepository.findPermissionCodesByRoleId(role.getId()));
        
        // 獲取用戶數量
        dto.setUserCount(userRoleRepository.countActiveUsersByRoleId(role.getId()));

        return dto;
    }
}