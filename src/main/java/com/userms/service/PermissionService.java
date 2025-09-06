package com.userms.service;

import com.userms.common.ErrorCodes;
import com.userms.common.PageResult;
import com.userms.dto.PermissionCreateDTO;
import com.userms.dto.PermissionDTO;
import com.userms.entity.Permission;
import com.userms.exception.BusinessException;
import com.userms.repository.PermissionRepository;
import com.userms.repository.RolePermissionRepository;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public PageResult<PermissionDTO> getAllPermissions(Pageable pageable) {
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        List<PermissionDTO> permissionDTOs = permissionPage.getContent().stream()
                .map(this::convertToPermissionDTO)
                .toList();
        
        return PageResult.of(permissionDTOs, permissionPage.getNumber(), permissionPage.getSize(), permissionPage.getTotalElements());
    }

    public PageResult<PermissionDTO> getActivePermissions(Pageable pageable) {
        Page<Permission> permissionPage = permissionRepository.findByIsActive(true, pageable);
        List<PermissionDTO> permissionDTOs = permissionPage.getContent().stream()
                .map(this::convertToPermissionDTO)
                .toList();
        
        return PageResult.of(permissionDTOs, permissionPage.getNumber(), permissionPage.getSize(), permissionPage.getTotalElements());
    }

    public PermissionDTO getPermissionById(String permissionId) {
        Permission permission = findPermissionById(permissionId);
        return convertToPermissionDTO(permission);
    }

    public List<PermissionDTO> searchPermissions(String keyword) {
        Page<Permission> permissionPage = permissionRepository.findByKeyword(keyword, Pageable.unpaged());
        return permissionPage.getContent().stream()
                .map(this::convertToPermissionDTO)
                .toList();
    }

    public List<PermissionDTO> getPermissionsByResource(String resourceName) {
        List<Permission> permissions = permissionRepository.findByResourceNameAndIsActive(resourceName, true);
        return permissions.stream().map(this::convertToPermissionDTO).toList();
    }

    @Transactional
    public PermissionDTO createPermission(PermissionCreateDTO createDTO) {
        log.info("創建權限: {}", createDTO.getPermissionCode());

        validateCreatePermission(createDTO);

        Permission permission = createPermissionFromDTO(createDTO);
        Permission savedPermission = permissionRepository.save(permission);

        log.info("權限創建成功: {}", savedPermission.getPermissionCode());
        return convertToPermissionDTO(savedPermission);
    }

    @Transactional
    public PermissionDTO updatePermission(String permissionId, PermissionDTO permissionDTO) {
        log.info("更新權限: {}", permissionId);

        Permission existingPermission = findPermissionById(permissionId);
        
        // 檢查權限代碼唯一性（排除當前權限）
        if (!existingPermission.getPermissionCode().equals(permissionDTO.getPermissionCode()) &&
            permissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())) {
            throw new BusinessException("權限代碼已存在", ErrorCodes.PERMISSION_NOT_FOUND);
        }

        // 更新權限資訊
        existingPermission.setPermissionName(permissionDTO.getPermissionName());
        existingPermission.setPermissionCode(permissionDTO.getPermissionCode());
        existingPermission.setResourceName(permissionDTO.getResourceName());
        existingPermission.setActionType(permissionDTO.getActionType());
        existingPermission.setIsActive(permissionDTO.getIsActive());
        existingPermission.setUpdatedTime(LocalDateTime.now());
        existingPermission.setUpdatedTs(System.currentTimeMillis());

        Permission savedPermission = permissionRepository.save(existingPermission);
        log.info("權限更新成功: {}", savedPermission.getPermissionCode());
        
        return convertToPermissionDTO(savedPermission);
    }

    @Transactional
    public void enablePermission(String permissionId) {
        log.info("啟用權限: {}", permissionId);
        Permission permission = findPermissionById(permissionId);
        permission.setIsActive(true);
        permission.setUpdatedTime(LocalDateTime.now());
        permission.setUpdatedTs(System.currentTimeMillis());
        permissionRepository.save(permission);
        log.info("權限啟用成功: {}", permission.getPermissionCode());
    }

    @Transactional
    public void disablePermission(String permissionId) {
        log.info("停用權限: {}", permissionId);
        Permission permission = findPermissionById(permissionId);
        permission.setIsActive(false);
        permission.setUpdatedTime(LocalDateTime.now());
        permission.setUpdatedTs(System.currentTimeMillis());
        permissionRepository.save(permission);
        log.info("權限停用成功: {}", permission.getPermissionCode());
    }

    @Transactional
    public void deletePermission(String permissionId) {
        log.info("刪除權限: {}", permissionId);
        Permission permission = findPermissionById(permissionId);
        
        // 檢查是否有角色使用此權限
        long roleCount = rolePermissionRepository.countRolesByPermissionId(permissionId);
        if (roleCount > 0) {
            throw new BusinessException("無法刪除權限，還有角色正在使用此權限", ErrorCodes.PERMISSION_NOT_FOUND);
        }
        
        // 刪除權限
        permissionRepository.delete(permission);
        log.info("權限刪除成功: {}", permission.getPermissionCode());
    }

    public List<String> getDistinctResourceNames() {
        return permissionRepository.findDistinctResourceNamesByIsActive(true);
    }

    public List<String> getDistinctActionTypes() {
        return permissionRepository.findDistinctActionTypesByIsActive(true);
    }

    private Permission findPermissionById(String permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException("權限不存在", ErrorCodes.PERMISSION_NOT_FOUND));
    }

    private void validateCreatePermission(PermissionCreateDTO createDTO) {
        if (permissionRepository.existsByPermissionCode(createDTO.getPermissionCode())) {
            throw new BusinessException("權限代碼已存在", ErrorCodes.PERMISSION_NOT_FOUND);
        }
    }

    private Permission createPermissionFromDTO(PermissionCreateDTO createDTO) {
        Permission permission = new Permission();
        permission.setPermissionName(createDTO.getPermissionName());
        permission.setPermissionCode(createDTO.getPermissionCode());
        permission.setResourceName(createDTO.getResourceName());
        permission.setActionType(createDTO.getActionType());
        permission.setIsActive(true);

        LocalDateTime now = LocalDateTime.now();
        long timestamp = System.currentTimeMillis();
        permission.setCreatedTime(now);
        permission.setCreatedTs(timestamp);
        permission.setUpdatedTime(now);
        permission.setUpdatedTs(timestamp);

        return permission;
    }

    private PermissionDTO convertToPermissionDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setPermissionName(permission.getPermissionName());
        dto.setPermissionCode(permission.getPermissionCode());
        dto.setResourceName(permission.getResourceName());
        dto.setActionType(permission.getActionType());
        dto.setIsActive(permission.getIsActive());
        dto.setCreatedTime(permission.getCreatedTime());
        dto.setUpdatedTime(permission.getUpdatedTime());

        // 獲取使用此權限的角色數量
        dto.setRoleCount(rolePermissionRepository.countActiveRolesByPermissionId(permission.getId()));

        return dto;
    }
}