package com.userms.repository;

import com.userms.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    // ===== 基本查詢方法 =====

    Optional<Permission> findByPermissionName(String permissionName);

    Optional<Permission> findByPermissionCode(String permissionCode);

    Optional<Permission> findByPermissionNameOrPermissionCode(String permissionName, String permissionCode);

    List<Permission> findByPermissionNameContainingIgnoreCase(String permissionName);

    boolean existsByPermissionName(String permissionName);

    boolean existsByPermissionCode(String permissionCode);

    boolean existsByPermissionNameOrPermissionCode(String permissionName, String permissionCode);

    // ===== 資源和操作查詢 =====

    List<Permission> findByResourceName(String resourceName);

    List<Permission> findByActionType(String actionType);

    List<Permission> findByResourceNameAndActionType(String resourceName, String actionType);

    List<Permission> findByResourceNameContainingIgnoreCase(String resourceName);

    List<Permission> findByActionTypeContainingIgnoreCase(String actionType);

    // ===== 啟用狀態查詢 =====

    List<Permission> findByIsActive(Boolean isActive);

    Page<Permission> findByIsActive(Boolean isActive, Pageable pageable);

    List<Permission> findByIsActiveOrderByResourceNameAscActionTypeAsc(Boolean isActive);

    long countByIsActive(Boolean isActive);

    // ===== 資源分組查詢 =====

    List<Permission> findByResourceNameAndIsActive(String resourceName, Boolean isActive);

    Page<Permission> findByResourceNameAndIsActive(String resourceName, Boolean isActive, Pageable pageable);

    @Query("SELECT DISTINCT p.resourceName FROM Permission p WHERE p.isActive = :isActive ORDER BY p.resourceName")
    List<String> findDistinctResourceNamesByIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT DISTINCT p.actionType FROM Permission p WHERE p.isActive = :isActive ORDER BY p.actionType")
    List<String> findDistinctActionTypesByIsActive(@Param("isActive") Boolean isActive);

    // ===== 模糊查詢 =====

    @Query("SELECT p FROM Permission p WHERE " +
            "(LOWER(p.permissionName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.permissionCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.resourceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.actionType) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "p.isActive = :isActive")
    Page<Permission> findByKeywordAndIsActive(@Param("keyword") String keyword,
                                              @Param("isActive") Boolean isActive,
                                              Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE " +
            "LOWER(p.permissionName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.permissionCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.resourceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.actionType) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Permission> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ===== 排序查詢 =====

    List<Permission> findAllByOrderByResourceNameAscActionTypeAsc();

    List<Permission> findAllByOrderByPermissionCodeAsc();

    Page<Permission> findAllByOrderByCreatedTimeDesc(Pageable pageable);

    // ===== 時間範圍查詢 =====

    List<Permission> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    Page<Permission> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // ===== 批次操作 =====

    @Modifying
    @Query("UPDATE Permission p SET p.isActive = :isActive, p.updatedTime = :updatedTime, p.updateUser = :updateUser WHERE p.id IN :ids")
    int updateActiveStatusByIds(@Param("ids") List<String> ids,
                                @Param("isActive") Boolean isActive,
                                @Param("updatedTime") LocalDateTime updatedTime,
                                @Param("updateUser") String updateUser);

    @Modifying
    @Query("UPDATE Permission p SET p.isActive = false, p.updatedTime = :updatedTime, p.updateUser = :updateUser WHERE p.id IN :ids")
    int deactivatePermissionsByIds(@Param("ids") List<String> ids,
                                   @Param("updatedTime") LocalDateTime updatedTime,
                                   @Param("updateUser") String updateUser);

    // ===== 統計查詢 =====

    @Query("SELECT COUNT(p) FROM Permission p WHERE p.createdTime >= :startTime")
    long countPermissionsCreatedSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(p) FROM Permission p WHERE p.isActive = true")
    long countActivePermissions();

    @Query("SELECT COUNT(p) FROM Permission p WHERE p.isActive = false")
    long countInactivePermissions();

    @Query("SELECT COUNT(p) FROM Permission p WHERE p.resourceName = :resourceName AND p.isActive = true")
    long countActivePermissionsByResource(@Param("resourceName") String resourceName);

    // ===== 複合條件查詢 =====

    @Query("SELECT p FROM Permission p WHERE " +
            "(:permissionName IS NULL OR LOWER(p.permissionName) LIKE LOWER(CONCAT('%', :permissionName, '%'))) AND " +
            "(:permissionCode IS NULL OR LOWER(p.permissionCode) LIKE LOWER(CONCAT('%', :permissionCode, '%'))) AND " +
            "(:resourceName IS NULL OR LOWER(p.resourceName) LIKE LOWER(CONCAT('%', :resourceName, '%'))) AND " +
            "(:actionType IS NULL OR LOWER(p.actionType) LIKE LOWER(CONCAT('%', :actionType, '%'))) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive) AND " +
            "(:startTime IS NULL OR p.createdTime >= :startTime) AND " +
            "(:endTime IS NULL OR p.createdTime <= :endTime)")
    Page<Permission> findPermissionsByCriteria(@Param("permissionName") String permissionName,
                                               @Param("permissionCode") String permissionCode,
                                               @Param("resourceName") String resourceName,
                                               @Param("actionType") String actionType,
                                               @Param("isActive") Boolean isActive,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               Pageable pageable);

    // ===== 資料驗證查詢 =====

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionName = :permissionName AND p.id != :excludeId")
    boolean existsByPermissionNameExcludingId(@Param("permissionName") String permissionName, @Param("excludeId") String excludeId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionCode = :permissionCode AND p.id != :excludeId")
    boolean existsByPermissionCodeExcludingId(@Param("permissionCode") String permissionCode, @Param("excludeId") String excludeId);

    // ===== 權限代碼模式查詢 =====

    List<Permission> findByPermissionCodeStartingWith(String prefix);

    List<Permission> findByPermissionCodeStartingWithAndIsActive(String prefix, Boolean isActive);

    @Query("SELECT p FROM Permission p WHERE p.permissionCode LIKE :pattern AND p.isActive = true ORDER BY p.permissionCode")
    List<Permission> findActivePermissionsByCodePattern(@Param("pattern") String pattern);

    // ===== 資源管理專用查詢 =====

    @Query("SELECT p FROM Permission p WHERE p.resourceName = :resourceName AND p.isActive = true ORDER BY p.actionType")
    List<Permission> findActivePermissionsByResource(@Param("resourceName") String resourceName);

    @Query("SELECT p FROM Permission p WHERE p.actionType = :actionType AND p.isActive = true ORDER BY p.resourceName")
    List<Permission> findActivePermissionsByAction(@Param("actionType") String actionType);

    @Query("SELECT p FROM Permission p WHERE p.resourceName IN :resourceNames AND p.isActive = true ORDER BY p.resourceName, p.actionType")
    List<Permission> findActivePermissionsByResources(@Param("resourceNames") List<String> resourceNames);
}