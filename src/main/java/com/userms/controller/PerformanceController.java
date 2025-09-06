package com.userms.controller;

import com.userms.common.ApiResponse;
import com.userms.common.PageResult;
import com.userms.dto.UserDTO;
import com.userms.service.UserPerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Tag(name = "⚡ 效能學習", description = "N+1 問題學習和查詢優化演示")
@RestController
@RequestMapping("/performance")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PerformanceController {

    private final UserPerformanceService userPerformanceService;

    @Operation(
            summary = "🐌 N+1 問題演示",
            description = """
                    ## N+1 查詢問題演示 (BAD)
                    
                    ### 問題說明
                    - 這個端點會故意觸發 N+1 查詢問題
                    - 1 次查詢獲取用戶列表
                    - N 次查詢獲取每個用戶的角色 (N = 用戶數量)
                    
                    ### 學習目的
                    - 理解什麼是 N+1 查詢問題
                    - 觀察查詢次數和執行時間
                    - 為後續優化做對比
                    
                    ### 注意
                    ⚠️ 此端點僅用於學習演示，生產環境中應避免使用
                    """)
    @GetMapping("/n-plus-1-demo")
    public ApiResponse<PageResult<UserDTO>> demonstrateN1Problem(
            @Parameter(description = "頁碼", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "5") @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        PageResult<UserDTO> result = userPerformanceService.getUsersWithN1Problem(pageable);
        
        return ApiResponse.success("N+1 問題演示完成 - 請查看日誌中的查詢次數", result);
    }

    @Operation(
            summary = "🚀 批次查詢優化",
            description = """
                    ## 批次查詢優化演示 (BETTER)
                    
                    ### 優化說明
                    - 1 次查詢獲取用戶列表
                    - 1 次批次查詢獲取所有用戶的角色
                    - 總查詢次數固定為 2，不隨用戶數量增長
                    
                    ### 優化技術
                    - 使用 IN 查詢批次獲取關聯數據
                    - 在應用層組裝數據
                    - 避免重複的數據庫訪問
                    
                    ### 效果
                    ✅ 大幅減少數據庫查詢次數和執行時間
                    """)
    @GetMapping("/batch-optimized")
    public ApiResponse<PageResult<UserDTO>> demonstrateBatchOptimization(
            @Parameter(description = "頁碼", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "5") @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        PageResult<UserDTO> result = userPerformanceService.getUsersOptimized(pageable);
        
        return ApiResponse.success("批次查詢優化完成 - 只執行了 2 次查詢", result);
    }

    @Operation(
            summary = "⭐ @EntityGraph 終極優化",
            description = """
                    ## @EntityGraph 優化演示 (BEST)
                    
                    ### 優化說明
                    - 使用 JPA @EntityGraph 注解
                    - 一次性 JOIN 查詢獲取用戶和角色
                    - 總查詢次數：1 (最優解)
                    
                    ### 優化技術
                    - @EntityGraph 聲明式關聯加載
                    - JPA 自動生成優化的 JOIN 查詢
                    - 避免 LazyInitializationException
                    
                    ### 效果
                    🏆 最少的查詢次數，最佳的執行效率
                    """)
    @GetMapping("/entity-graph")
    public ApiResponse<PageResult<UserDTO>> demonstrateEntityGraphOptimization(
            @Parameter(description = "頁碼", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "5") @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        PageResult<UserDTO> result = userPerformanceService.getUsersWithEntityGraph(pageable);
        
        return ApiResponse.success("@EntityGraph 優化完成 - 只執行了 1 次查詢 (最優！)", result);
    }

    @Operation(
            summary = "📊 效能比較測試",
            description = """
                    ## 三種方法的效能比較
                    
                    ### 測試內容
                    - 同時執行三種查詢方法
                    - 記錄執行時間和查詢次數
                    - 計算效能提升百分比
                    
                    ### 學習價值
                    - 直觀理解不同優化方案的效果
                    - 掌握 JPA 查詢優化的最佳實踐
                    - 了解 N+1 問題的嚴重性和解決方案
                    
                    ### 注意
                    此操作會執行較多查詢，建議在學習環境使用
                    """)
    @PostMapping("/comparison")
    public ApiResponse<String> performanceComparison(
            @Parameter(description = "頁碼", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "3") @RequestParam(defaultValue = "3") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        userPerformanceService.performanceComparison(pageable);
        
        return ApiResponse.success("效能比較測試完成！請查看日誌中的詳細分析結果");
    }

    @Operation(
            summary = "📚 N+1 問題學習指南",
            description = "獲取 N+1 問題的學習指南和優化建議")
    @GetMapping("/learning-guide")
    public ApiResponse<Object> getLearningGuide() {
        
        var guide = java.util.Map.of(
            "什麼是N+1問題", "N+1查詢問題是指：執行1次查詢獲取N條記錄，然後為每條記錄執行1次額外查詢獲取關聯數據，總共N+1次查詢",
            "問題嚴重性", "隨著數據量增長，查詢次數線性增長，嚴重影響應用性能",
            "識別方法", java.util.List.of(
                "啟用SQL日誌觀察查詢次數",
                "使用p6spy等工具監控SQL執行",
                "關注LazyInitializationException異常"
            ),
            "解決方案", java.util.Map.of(
                "方案1", "批次查詢 - 使用IN查詢批次獲取關聯數據",
                "方案2", "@EntityGraph - JPA聲明式關聯加載",
                "方案3", "JOIN FETCH - JPQL手動JOIN查詢",
                "方案4", "數據傳輸層優化 - 避免在循環中查詢"
            ),
            "最佳實踐", java.util.List.of(
                "優先使用@EntityGraph解決簡單關聯",
                "複雜查詢使用JPQL JOIN FETCH",
                "批次查詢適用於動態關聯場景",
                "始終監控和測量查詢效能"
            ),
            "學習建議", java.util.List.of(
                "先理解問題：使用/performance/n-plus-1-demo",
                "學習批次優化：使用/performance/batch-optimized", 
                "掌握@EntityGraph：使用/performance/entity-graph",
                "綜合比較：使用/performance/comparison"
            )
        );
        
        return ApiResponse.success("N+1 問題學習指南", guide);
    }
}