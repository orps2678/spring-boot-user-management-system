package com.userms.repository;

import com.userms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // ===== 基本查詢方法 =====

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    // ===== 啟用狀態查詢 =====

    List<User> findByIsActive(Boolean isActive);

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    long countByIsActive(Boolean isActive);

    // ===== 模糊查詢 =====

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "u.isActive = :isActive")
    Page<User> findByKeywordAndIsActive(@Param("keyword") String keyword,
                                        @Param("isActive") Boolean isActive,
                                        Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ===== 時間範圍查詢 =====

    List<User> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    Page<User> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    List<User> findByCreatedTimeAfter(LocalDateTime since);

    // ===== 批次操作 =====

    @Modifying
    @Query("UPDATE User u SET u.isActive = :isActive, u.updatedTime = :updatedTime, u.updateUser = :updateUser WHERE u.id IN :ids")
    int updateActiveStatusByIds(@Param("ids") List<String> ids,
                                @Param("isActive") Boolean isActive,
                                @Param("updatedTime") LocalDateTime updatedTime,
                                @Param("updateUser") String updateUser);

    @Modifying
    @Query("UPDATE User u SET u.isActive = false, u.updatedTime = :updatedTime, u.updateUser = :updateUser WHERE u.id IN :ids")
    int deactivateUsersByIds(@Param("ids") List<String> ids,
                             @Param("updatedTime") LocalDateTime updatedTime,
                             @Param("updateUser") String updateUser);

    // ===== 統計查詢 =====

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdTime >= :startTime")
    long countUsersCreatedSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    long countInactiveUsers();

    // ===== 複合條件查詢 =====

    @Query("SELECT u FROM User u WHERE " +
            "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
            "(:startTime IS NULL OR u.createdTime >= :startTime) AND " +
            "(:endTime IS NULL OR u.createdTime <= :endTime)")
    Page<User> findUsersByCriteria(@Param("username") String username,
                                   @Param("email") String email,
                                   @Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("isActive") Boolean isActive,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   Pageable pageable);

    // ===== 資料驗證查詢 =====

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.id != :excludeId")
    boolean existsByUsernameExcludingId(@Param("username") String username, @Param("excludeId") String excludeId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("excludeId") String excludeId);

    // ===== 搜索方法 =====
    
    List<User> findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
            String username, String email, String firstName, String lastName);

    // ===== N+1 問題優化查詢 =====
    
    /**
     * 使用 @EntityGraph 解決 N+1 問題
     * 一次性加載用戶和其角色關聯，避免 N+1 查詢問題
     */
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    @Query("SELECT u FROM User u")
    Page<User> findAllWithRoles(Pageable pageable);
    
    /**
     * 根據啟用狀態使用 @EntityGraph 查詢用戶和角色
     */
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive")
    Page<User> findByIsActiveWithRoles(@Param("isActive") Boolean isActive, Pageable pageable);
    
    /**
     * 根據關鍵字搜索並使用 @EntityGraph 加載角色
     */
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> findByKeywordWithRoles(@Param("keyword") String keyword);
}