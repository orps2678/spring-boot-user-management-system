package com.userms.repository;

import com.userms.entity.Permission;
import com.userms.entity.Role;
import com.userms.entity.RolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionId> {

    // ===== 基本查詢方法 =====

    List<RolePermission> findByIdRoleId(String roleId);

    List<RolePermission> findByIdPermissionId(String permissionId);

    Optional<RolePermission> findByIdRoleIdAndIdPermissionId(String roleId, String permissionId);

    boolean existsByIdRoleIdAndIdPermissionId(String roleId, String permissionId);

    // ===== 角色相關查詢 =====

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") String roleId);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId")
    Page<Permission> findPermissionsByRoleId(@Param("roleId") String roleId, Pageable pageable);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.isActive = :isActive")
    List<Permission> findPermissionsByRoleIdAndIsActive(@Param("roleId") String roleId, @Param("isActive") Boolean isActive);

    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.id.roleId = :roleId")
    long countPermissionsByRoleId(@Param("roleId") String roleId);

    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.isActive = true")
    long countActivePermissionsByRoleId(@Param("roleId") String roleId);

    // ===== 權限相關查詢 =====

    @Query("SELECT rp.role FROM RolePermission rp WHERE rp.id.permissionId = :permissionId")
    List<Role> findRolesByPermissionId(@Param("permissionId") String permissionId);

    @Query("SELECT rp.role FROM RolePermission rp WHERE rp.id.permissionId = :permissionId")
    Page<Role> findRolesByPermissionId(@Param("permissionId") String permissionId, Pageable pageable);

    @Query("SELECT rp.role FROM RolePermission rp WHERE rp.id.permissionId = :permissionId AND rp.role.isActive = :isActive")
    List<Role> findRolesByPermissionIdAndIsActive(@Param("permissionId") String permissionId, @Param("isActive") Boolean isActive);

    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.id.permissionId = :permissionId")
    long countRolesByPermissionId(@Param("permissionId") String permissionId);

    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.id.permissionId = :permissionId AND rp.role.isActive = true")
    long countActiveRolesByPermissionId(@Param("permissionId") String permissionId);

    // ===== 權限代碼查詢 =====

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.permissionCode = :permissionCode")
    Optional<Permission> findPermissionByRoleIdAndPermissionCode(@Param("roleId") String roleId, @Param("permissionCode") String permissionCode);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.permissionCode IN :permissionCodes")
    List<Permission> findPermissionsByRoleIdAndPermissionCodes(@Param("roleId") String roleId, @Param("permissionCodes") List<String> permissionCodes);

    @Query("SELECT rp.permission.permissionCode FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.isActive = true")
    List<String> findPermissionCodesByRoleId(@Param("roleId") String roleId);

    // ===== 資源權限查詢 =====

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.resourceName = :resourceName AND rp.permission.isActive = true")
    List<Permission> findPermissionsByRoleIdAndResource(@Param("roleId") String roleId, @Param("resourceName") String resourceName);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.actionType = :actionType AND rp.permission.isActive = true")
    List<Permission> findPermissionsByRoleIdAndAction(@Param("roleId") String roleId, @Param("actionType") String actionType);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.permission.resourceName = :resourceName AND rp.permission.actionType = :actionType AND rp.permission.isActive = true")
    Optional<Permission> findPermissionByRoleIdAndResourceAndAction(@Param("roleId") String roleId, @Param("resourceName") String resourceName, @Param("actionType") String actionType);

    // ===== 批次操作 =====

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.id.roleId = :roleId")
    int deleteAllByRoleId(@Param("roleId") String roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.id.permissionId = :permissionId")
    int deleteAllByPermissionId(@Param("permissionId") String permissionId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.id.roleId = :roleId AND rp.id.permissionId IN :permissionIds")
    int deleteByRoleIdAndPermissionIds(@Param("roleId") String roleId, @Param("permissionIds") List<String> permissionIds);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.id.roleId IN :roleIds AND rp.id.permissionId = :permissionId")
    int deleteByRoleIdsAndPermissionId(@Param("roleIds") List<String> roleIds, @Param("permissionId") String permissionId);

    // ===== 權限檢查查詢 =====

    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END FROM RolePermission rp " +
            "WHERE rp.id.roleId = :roleId AND rp.permission.permissionCode = :permissionCode AND rp.permission.isActive = true")
    boolean hasPermission(@Param("roleId") String roleId, @Param("permissionCode") String permissionCode);

    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END FROM RolePermission rp " +
            "WHERE rp.id.roleId = :roleId AND rp.permission.permissionCode IN :permissionCodes AND rp.permission.isActive = true")
    boolean hasAnyPermission(@Param("roleId") String roleId, @Param("permissionCodes") List<String> permissionCodes);

    @Query("SELECT COUNT(rp) FROM RolePermission rp " +
            "WHERE rp.id.roleId = :roleId AND rp.permission.permissionCode IN :permissionCodes AND rp.permission.isActive = true")
    long countMatchingPermissions(@Param("roleId") String roleId, @Param("permissionCodes") List<String> permissionCodes);

    // ===== 使用者權限查詢（透過角色） =====

    @Query("SELECT DISTINCT rp.permission FROM UserRole ur " +
            "JOIN RolePermission rp ON ur.id.roleId = rp.id.roleId " +
            "WHERE ur.id.userId = :userId AND ur.role.isActive = true AND rp.permission.isActive = true")
    List<Permission> findPermissionsByUserId(@Param("userId") String userId);

    @Query("SELECT DISTINCT rp.permission.permissionCode FROM UserRole ur " +
            "JOIN RolePermission rp ON ur.id.roleId = rp.id.roleId " +
            "WHERE ur.id.userId = :userId AND ur.role.isActive = true AND rp.permission.isActive = true")
    List<String> findPermissionCodesByUserId(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END FROM UserRole ur " +
            "JOIN RolePermission rp ON ur.id.roleId = rp.id.roleId " +
            "WHERE ur.id.userId = :userId AND rp.permission.permissionCode = :permissionCode " +
            "AND ur.role.isActive = true AND rp.permission.isActive = true")
    boolean userHasPermission(@Param("userId") String userId, @Param("permissionCode") String permissionCode);

    // ===== 統計查詢 =====

    @Query("SELECT COUNT(DISTINCT rp.id.roleId) FROM RolePermission rp WHERE rp.permission.isActive = true")
    long countDistinctRolesWithActivePermissions();

    @Query("SELECT COUNT(DISTINCT rp.id.permissionId) FROM RolePermission rp WHERE rp.role.isActive = true")
    long countDistinctPermissionsWithActiveRoles();

    @Query("SELECT rp.permission.permissionName, COUNT(rp) FROM RolePermission rp " +
            "WHERE rp.role.isActive = true AND rp.permission.isActive = true " +
            "GROUP BY rp.permission.permissionName ORDER BY COUNT(rp) DESC")
    List<Object[]> findPermissionUsageStatistics();

    // ===== 複合查詢 =====

    @Query("SELECT rp FROM RolePermission rp " +
            "JOIN rp.role r " +
            "JOIN rp.permission p " +
            "WHERE (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:permissionId IS NULL OR p.id = :permissionId) " +
            "AND (:roleActive IS NULL OR r.isActive = :roleActive) " +
            "AND (:permissionActive IS NULL OR p.isActive = :permissionActive)")
    Page<RolePermission> findRolePermissionsByCriteria(@Param("roleId") String roleId,
                                                       @Param("permissionId") String permissionId,
                                                       @Param("roleActive") Boolean roleActive,
                                                       @Param("permissionActive") Boolean permissionActive,
                                                       Pageable pageable);
}