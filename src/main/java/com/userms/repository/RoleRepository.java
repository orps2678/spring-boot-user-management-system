package com.userms.repository;

import com.userms.entity.Role;
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
public interface RoleRepository extends JpaRepository<Role, String> {

    // ===== 基本查詢方法 =====

    Optional<Role> findByRoleName(String roleName);

    Optional<Role> findByRoleCode(String roleCode);

    Optional<Role> findByRoleNameOrRoleCode(String roleName, String roleCode);

    List<Role> findByRoleNameContainingIgnoreCase(String roleName);

    boolean existsByRoleName(String roleName);

    boolean existsByRoleCode(String roleCode);

    boolean existsByRoleNameOrRoleCode(String roleName, String roleCode);

    // ===== 啟用狀態查詢 =====

    List<Role> findByIsActive(Boolean isActive);

    Page<Role> findByIsActive(Boolean isActive, Pageable pageable);

    List<Role> findByIsActiveOrderByRoleNameAsc(Boolean isActive);

    long countByIsActive(Boolean isActive);

    // ===== 模糊查詢 =====

    @Query("SELECT r FROM Role r WHERE " +
            "(LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.roleCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "r.isActive = :isActive")
    Page<Role> findByKeywordAndIsActive(@Param("keyword") String keyword,
                                        @Param("isActive") Boolean isActive,
                                        Pageable pageable);

    @Query("SELECT r FROM Role r WHERE " +
            "LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.roleCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Role> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ===== 排序查詢 =====

    List<Role> findAllByOrderByRoleNameAsc();

    List<Role> findAllByOrderByRoleCodeAsc();

    Page<Role> findAllByOrderByCreatedTimeDesc(Pageable pageable);

    // ===== 時間範圍查詢 =====

    List<Role> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    Page<Role> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // ===== 批次操作 =====

    @Modifying
    @Query("UPDATE Role r SET r.isActive = :isActive, r.updatedTime = :updatedTime, r.updateUser = :updateUser WHERE r.id IN :ids")
    int updateActiveStatusByIds(@Param("ids") List<String> ids,
                                @Param("isActive") Boolean isActive,
                                @Param("updatedTime") LocalDateTime updatedTime,
                                @Param("updateUser") String updateUser);

    @Modifying
    @Query("UPDATE Role r SET r.isActive = false, r.updatedTime = :updatedTime, r.updateUser = :updateUser WHERE r.id IN :ids")
    int deactivateRolesByIds(@Param("ids") List<String> ids,
                             @Param("updatedTime") LocalDateTime updatedTime,
                             @Param("updateUser") String updateUser);

    // ===== 統計查詢 =====

    @Query("SELECT COUNT(r) FROM Role r WHERE r.createdTime >= :startTime")
    long countRolesCreatedSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(r) FROM Role r WHERE r.isActive = true")
    long countActiveRoles();

    @Query("SELECT COUNT(r) FROM Role r WHERE r.isActive = false")
    long countInactiveRoles();

    // ===== 複合條件查詢 =====

    @Query("SELECT r FROM Role r WHERE " +
            "(:roleName IS NULL OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :roleName, '%'))) AND " +
            "(:roleCode IS NULL OR LOWER(r.roleCode) LIKE LOWER(CONCAT('%', :roleCode, '%'))) AND " +
            "(:description IS NULL OR LOWER(r.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:isActive IS NULL OR r.isActive = :isActive) AND " +
            "(:startTime IS NULL OR r.createdTime >= :startTime) AND " +
            "(:endTime IS NULL OR r.createdTime <= :endTime)")
    Page<Role> findRolesByCriteria(@Param("roleName") String roleName,
                                   @Param("roleCode") String roleCode,
                                   @Param("description") String description,
                                   @Param("isActive") Boolean isActive,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   Pageable pageable);

    // ===== 資料驗證查詢 =====

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleName = :roleName AND r.id != :excludeId")
    boolean existsByRoleNameExcludingId(@Param("roleName") String roleName, @Param("excludeId") String excludeId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleCode = :roleCode AND r.id != :excludeId")
    boolean existsByRoleCodeExcludingId(@Param("roleCode") String roleCode, @Param("excludeId") String excludeId);

    // ===== 角色代碼模式查詢 =====

    List<Role> findByRoleCodeStartingWith(String prefix);

    List<Role> findByRoleCodeStartingWithAndIsActive(String prefix, Boolean isActive);

    @Query("SELECT r FROM Role r WHERE r.roleCode LIKE :pattern AND r.isActive = true ORDER BY r.roleCode")
    List<Role> findActiveRolesByCodePattern(@Param("pattern") String pattern);
}