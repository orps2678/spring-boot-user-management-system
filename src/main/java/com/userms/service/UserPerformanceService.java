package com.userms.service;

import com.userms.common.PageResult;
import com.userms.dto.UserDTO;
import com.userms.entity.Role;
import com.userms.entity.User;
import com.userms.repository.UserRepository;
import com.userms.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用戶效能服務 - 專門用於演示和解決 N+1 問題
 * 
 * 學習目標：
 * 1. 識別 N+1 問題案例
 * 2. 使用 @EntityGraph 解決方案
 * 3. 使用批次查詢優化
 * 4. 效能比較分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPerformanceService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 方法 1: 存在 N+1 問題的版本 (BAD)
     * 
     * 問題分析：
     * - 1 次查詢獲取用戶列表
     * - N 次查詢獲取每個用戶的角色 (N = 用戶數量)
     * - 總查詢次數：1 + N
     * 
     * 示例：如果有 100 個用戶，會執行 101 次 SQL 查詢！
     */
    public PageResult<UserDTO> getUsersWithN1Problem(Pageable pageable) {
        log.warn("=== N+1 問題演示開始 ===");
        long startTime = System.currentTimeMillis();
        
        Page<User> userPage = userRepository.findAll(pageable);
        log.info("第1次查詢：獲取用戶列表 - 查詢 {} 個用戶", userPage.getContent().size());
        
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(user -> {
                    // 每個用戶都會觸發一次額外的查詢！這就是 N+1 問題
                    UserDTO dto = convertToUserDTO(user);
                    log.debug("為用戶 {} 執行額外查詢獲取角色", user.getUsername());
                    return dto;
                })
                .toList();
        
        long endTime = System.currentTimeMillis();
        log.warn("=== N+1 問題演示結束 ===");
        log.warn("總耗時：{} ms，預計執行了 {} 次 SQL 查詢", 
                endTime - startTime, 1 + userPage.getContent().size());
        
        return PageResult.of(userDTOs, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }

    /**
     * 方法 2: 使用批次查詢優化的版本 (GOOD)
     * 
     * 解決方案：
     * - 1 次查詢獲取用戶列表
     * - 1 次查詢獲取所有用戶的角色關聯
     * - 總查詢次數：2 (固定)
     * 
     * 優化效果：無論多少用戶，只執行 2 次 SQL 查詢！
     */
    public PageResult<UserDTO> getUsersOptimized(Pageable pageable) {
        log.info("=== 批次查詢優化演示開始 ===");
        long startTime = System.currentTimeMillis();
        
        // 第1次查詢：獲取用戶列表
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> users = userPage.getContent();
        log.info("第1次查詢：獲取用戶列表 - 查詢 {} 個用戶", users.size());
        
        if (users.isEmpty()) {
            return PageResult.of(List.of(), userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
        }
        
        // 第2次查詢：批次獲取所有用戶的角色
        List<String> userIds = users.stream().map(User::getId).toList();
        Map<String, List<String>> userRolesMap = getUserRolesBatch(userIds);
        log.info("第2次查詢：批次獲取所有用戶角色 - 共 {} 個用戶的角色關聯", userIds.size());
        
        // 組裝 DTO，不再觸發額外查詢
        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO dto = convertToUserDTOWithoutQuery(user);
                    dto.setRoles(userRolesMap.getOrDefault(user.getId(), List.of()));
                    return dto;
                })
                .toList();
        
        long endTime = System.currentTimeMillis();
        log.info("=== 批次查詢優化演示結束 ===");
        log.info("總耗時：{} ms，只執行了 2 次 SQL 查詢", endTime - startTime);
        
        return PageResult.of(userDTOs, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }

    /**
     * 方法 3: 使用 @EntityGraph 的版本 (BEST)
     * 
     * 解決方案：
     * - 使用 @EntityGraph 在單次查詢中 JOIN 獲取關聯數據
     * - 總查詢次數：1
     * 
     * 這個方法需要在 Repository 中定義，見 UserRepository.findAllWithRoles()
     */
    public PageResult<UserDTO> getUsersWithEntityGraph(Pageable pageable) {
        log.info("=== @EntityGraph 優化演示開始 ===");
        long startTime = System.currentTimeMillis();
        
        // 使用 @EntityGraph 一次性加載用戶和角色關聯
        Page<User> userPage = userRepository.findAllWithRoles(pageable);
        log.info("使用 @EntityGraph 一次性查詢：獲取 {} 個用戶及其角色", userPage.getContent().size());
        
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::convertToUserDTOFromEntityGraph)
                .toList();
        
        long endTime = System.currentTimeMillis();
        log.info("=== @EntityGraph 優化演示結束 ===");
        log.info("總耗時：{} ms，只執行了 1 次 SQL 查詢（最優！）", endTime - startTime);
        
        return PageResult.of(userDTOs, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }

    /**
     * 效能比較方法
     * 用於比較三種方法的效能差異
     */
    public void performanceComparison(Pageable pageable) {
        log.info("========================================");
        log.info("開始 N+1 問題效能比較測試");
        log.info("========================================");
        
        // 測試 N+1 問題版本
        long start1 = System.currentTimeMillis();
        getUsersWithN1Problem(pageable);
        long time1 = System.currentTimeMillis() - start1;
        
        // 測試批次查詢優化版本
        long start2 = System.currentTimeMillis();
        getUsersOptimized(pageable);
        long time2 = System.currentTimeMillis() - start2;
        
        // 測試 @EntityGraph 版本
        long start3 = System.currentTimeMillis();
        getUsersWithEntityGraph(pageable);
        long time3 = System.currentTimeMillis() - start3;
        
        log.info("========================================");
        log.info("效能比較結果：");
        log.info("N+1 問題版本：{} ms", time1);
        log.info("批次查詢優化：{} ms (提升 {}%)", time2, 
                Math.round((double)(time1 - time2) / time1 * 100));
        log.info("@EntityGraph 優化：{} ms (提升 {}%)", time3, 
                Math.round((double)(time1 - time3) / time1 * 100));
        log.info("========================================");
    }

    /**
     * 批次獲取用戶角色的輔助方法
     */
    private Map<String, List<String>> getUserRolesBatch(List<String> userIds) {
        List<Object[]> userRoles = userRoleRepository.findRoleCodesByUserIds(userIds);
        
        return userRoles.stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[0], // userId
                        Collectors.mapping(
                                row -> (String) row[1], // roleCode
                                Collectors.toList()
                        )
                ));
    }

    /**
     * 原始的轉換方法 - 會觸發額外查詢 (N+1 問題)
     */
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

        // 這裡會觸發額外的查詢！
        dto.setRoles(userRoleRepository.findRoleCodesByUserId(user.getId()));

        return dto;
    }

    /**
     * 優化的轉換方法 - 不觸發額外查詢
     */
    private UserDTO convertToUserDTOWithoutQuery(User user) {
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
        
        // 角色由外部設置，避免 N+1 問題
        return dto;
    }

    /**
     * 從 @EntityGraph 加載的實體轉換 DTO
     */
    private UserDTO convertToUserDTOFromEntityGraph(User user) {
        UserDTO dto = convertToUserDTOWithoutQuery(user);
        
        // 從已經加載的關聯中獲取角色，不會觸發額外查詢
        List<String> roleCodes = user.getUserRoles().stream()
                .filter(ur -> ur.getRole().getIsActive())
                .map(ur -> ur.getRole().getRoleCode())
                .toList();
        
        dto.setRoles(roleCodes);
        return dto;
    }
}