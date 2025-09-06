package com.userms.repository;

import com.userms.entity.Role;
import com.userms.entity.User;
import com.userms.entity.UserRole;
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
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    // ===== 基本查詢方法 =====

    List<UserRole> findByIdUserId(String userId);

    List<UserRole> findByIdRoleId(String roleId);

    Optional<UserRole> findByIdUserIdAndIdRoleId(String userId, String roleId);

    boolean existsByIdUserIdAndIdRoleId(String userId, String roleId);

    // ===== 使用者相關查詢 =====

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.id.userId = :userId")
    List<Role> findRolesByUserId(@Param("userId") String userId);

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.id.userId = :userId")
    Page<Role> findRolesByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.id.userId = :userId AND ur.role.isActive = :isActive")
    List<Role> findRolesByUserIdAndIsActive(@Param("userId") String userId, @Param("isActive") Boolean isActive);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.id.userId = :userId")
    long countRolesByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.id.userId = :userId AND ur.role.isActive = true")
    long countActiveRolesByUserId(@Param("userId") String userId);

    // ===== 角色相關查詢 =====

    @Query("SELECT ur.user FROM UserRole ur WHERE ur.id.roleId = :roleId")
    List<User> findUsersByRoleId(@Param("roleId") String roleId);

    @Query("SELECT ur.user FROM UserRole ur WHERE ur.id.roleId = :roleId")
    Page<User> findUsersByRoleId(@Param("roleId") String roleId, Pageable pageable);

    @Query("SELECT ur.user FROM UserRole ur WHERE ur.id.roleId = :roleId AND ur.user.isActive = :isActive")
    List<User> findUsersByRoleIdAndIsActive(@Param("roleId") String roleId, @Param("isActive") Boolean isActive);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.id.roleId = :roleId")
    long countUsersByRoleId(@Param("roleId") String roleId);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.id.roleId = :roleId AND ur.user.isActive = true")
    long countActiveUsersByRoleId(@Param("roleId") String roleId);

    // ===== 角色代碼查詢 =====

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.id.userId = :userId AND ur.role.roleCode = :roleCode")
    Optional<Role> findRoleByUserIdAndRoleCode(@Param("userId") String userId, @Param("roleCode") String roleCode);

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.id.userId = :userId AND ur.role.roleCode IN :roleCodes")
    List<Role> findRolesByUserIdAndRoleCodes(@Param("userId") String userId, @Param("roleCodes") List<String> roleCodes);

    @Query("SELECT ur.role.roleCode FROM UserRole ur WHERE ur.id.userId = :userId AND ur.role.isActive = true")
    List<String> findRoleCodesByUserId(@Param("userId") String userId);

    // ===== 批次操作 =====

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId = :userId")
    int deleteAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.roleId = :roleId")
    int deleteAllByRoleId(@Param("roleId") String roleId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId = :userId AND ur.id.roleId IN :roleIds")
    int deleteByUserIdAndRoleIds(@Param("userId") String userId, @Param("roleIds") List<String> roleIds);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId IN :userIds AND ur.id.roleId = :roleId")
    int deleteByUserIdsAndRoleId(@Param("userIds") List<String> userIds, @Param("roleId") String roleId);

    // ===== 權限檢查查詢 =====

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
            "WHERE ur.id.userId = :userId AND ur.role.roleCode = :roleCode AND ur.role.isActive = true")
    boolean hasRole(@Param("userId") String userId, @Param("roleCode") String roleCode);

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
            "WHERE ur.id.userId = :userId AND ur.role.roleCode IN :roleCodes AND ur.role.isActive = true")
    boolean hasAnyRole(@Param("userId") String userId, @Param("roleCodes") List<String> roleCodes);

    @Query("SELECT COUNT(ur) FROM UserRole ur " +
            "WHERE ur.id.userId = :userId AND ur.role.roleCode IN :roleCodes AND ur.role.isActive = true")
    long countMatchingRoles(@Param("userId") String userId, @Param("roleCodes") List<String> roleCodes);

    // ===== 統計查詢 =====

    @Query("SELECT COUNT(DISTINCT ur.id.userId) FROM UserRole ur WHERE ur.role.isActive = true")
    long countDistinctUsersWithActiveRoles();

    @Query("SELECT COUNT(DISTINCT ur.id.roleId) FROM UserRole ur WHERE ur.user.isActive = true")
    long countDistinctRolesWithActiveUsers();

    @Query("SELECT ur.role.roleName, COUNT(ur) FROM UserRole ur " +
            "WHERE ur.user.isActive = true AND ur.role.isActive = true " +
            "GROUP BY ur.role.roleName ORDER BY COUNT(ur) DESC")
    List<Object[]> findRoleUsageStatistics();

    // ===== 複合查詢 =====

    @Query("SELECT ur FROM UserRole ur " +
            "JOIN ur.user u " +
            "JOIN ur.role r " +
            "WHERE (:userId IS NULL OR u.id = :userId) " +
            "AND (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:userActive IS NULL OR u.isActive = :userActive) " +
            "AND (:roleActive IS NULL OR r.isActive = :roleActive)")
    Page<UserRole> findUserRolesByCriteria(@Param("userId") String userId,
                                           @Param("roleId") String roleId,
                                           @Param("userActive") Boolean userActive,
                                           @Param("roleActive") Boolean roleActive,
                                           Pageable pageable);

    // ===== 批次查詢優化 - 解決 N+1 問題 =====
    
    /**
     * 批次獲取多個用戶的角色代碼
     * 用於解決 N+1 查詢問題
     * 
     * @param userIds 用戶 ID 列表
     * @return 返回 [userId, roleCode] 的對象數組列表
     */
    @Query("SELECT ur.id.userId, r.roleCode FROM UserRole ur " +
           "JOIN ur.role r " +
           "WHERE ur.id.userId IN :userIds AND r.isActive = true")
    List<Object[]> findRoleCodesByUserIds(@Param("userIds") List<String> userIds);
    
    /**
     * 批次獲取多個角色的用戶 ID
     * 
     * @param roleIds 角色 ID 列表  
     * @return 返回 [roleId, userId] 的對象數組列表
     */
    @Query("SELECT ur.id.roleId, ur.id.userId FROM UserRole ur " +
           "JOIN ur.user u " +
           "WHERE ur.id.roleId IN :roleIds AND u.isActive = true")
    List<Object[]> findUserIdsByRoleIds(@Param("roleIds") List<String> roleIds);
}