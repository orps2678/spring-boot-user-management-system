# 開發日誌 (CHANGELOG)

## [Unreleased] - 進行中

### 計劃中
- JPA Entity 實體類別建立
- Repository 層實作
- Service 業務邏輯層實作
- Controller REST API 實作

---

## [0.1.0] - 2025-09-05 基礎架構完成

### 重要里程碑
- 完成專案基礎架構搭建
- 建立完整的 RBAC 資料庫架構
- Docker 容器化開發環境就緒

### Added 新增功能
- Maven 專案初始化，包含企業級依賴配置
- Spring Boot 3.5.5 + Java 17 環境
- OAuth2 Resource Server 安全配置
- SpringDoc OpenAPI 3 API 文檔整合
- Oracle XE 21c 容器化資料庫
- Flyway 資料庫版本控制
- 多環境配置支援 (dev/sit/uat/prod)
- HikariCP 連線池優化配置
- p6spy SQL 監控工具配置
- JSON 結構化日誌

### Database 資料庫
- 創建完整的 RBAC 資料模型：
    - `users` 表：用戶基本資訊，包含軟刪除、樂觀鎖
    - `roles` 表：角色管理，支援系統角色和自定義角色
    - `permissions` 表：細粒度權限控制
    - `user_roles` 表：用戶角色多對多關聯
    - `role_permissions` 表：角色權限多對多關聯
- 自動創建系統預設角色：管理員、一般用戶、訪客
- 完整的約束、索引和觸發器
- 審計欄位自動管理 (created_time, updated_time, version)

### Configuration 配置
- 安全配置：允許 Swagger UI 和健康檢查端點訪問
- 資料庫配置：多環境連線字串和連線池參數
- 日誌配置：開發環境詳細日誌，生產環境精簡日誌
- Flyway 配置：基線遷移和驗證策略

### Development Environment 開發環境
- Docker Compose 一鍵啟動 Oracle XE
- IntelliJ IDEA 運行配置設定
- 環境變數和 VM 選項配置
- 自動化的資料庫用戶創建腳本

### Fixed 修復問題
- 解決 Oracle 21c 多租戶架構 CDB/PDB 用戶創建問題
- 修復 Flyway schema 權限不足問題
- 移除 Hibernate 不必要的方言配置警告
- 優化 Spring Security 過濾器鏈配置

### Technical Decisions 技術決策
- 選擇 Oracle XE 21c 作為學習資料庫（企業級標準）
- 採用 OAuth2 Resource Server 而非傳統 Session 認證
- 使用 Flyway 而非 Hibernate DDL Auto 管理資料庫結構
- 實施嚴格的多環境配置分離
- 採用企業級命名規範和包結構

### Performance 效能優化
- HikariCP 連線池參數調優
- 資料庫索引策略規劃
- SQL 執行監控機制建立
- JPA 查詢效能基準準備

### Documentation 文檔
- 完整的 README.md 專案說明
- API 端點規範定義
- 資料庫架構文檔
- 開發環境設置指南

---

## 開發統計

### 總計時間
- **Week 1-2**: 約 16 小時
    - 環境設置: 4 小時
    - 資料庫設計: 6 小時
    - Spring Boot 配置: 4 小時
    - 文檔撰寫: 2 小時

### 程式碼統計
- Java 類別: 2 個 (Application + SecurityConfig)
- 配置檔案: 6 個 (application*.yml)
- Flyway 遷移腳本: 3 個
- Docker 配置: 1 個
- 總行數: 約 800 行

### 學習成果
- 掌握 Spring Boot 3.x 新特性
- 深入理解 Oracle 多租戶架構
- 熟悉 Flyway 資料庫版本控制
- 實踐企業級配置管理
- 學會容器化開發環境搭建

---

## 下一步計劃

### Week 3 目標 (預計 2025-09-06 ~ 2025-09-12)
1. 建立 JPA Entity 實體類別
2. 實作 Repository 介面
3. 配置 p6spy 詳細監控
4. 開發基礎 CRUD 功能
5. 建立查詢效能基準

### Week 4 目標
1. 深入學習 N+1 問題
2. 實施 @EntityGraph 優化
3. 角色權限關聯查詢優化
4. 單元測試框架建立

---

*更新頻率：每完成一個重要功能或解決關鍵問題時更新*