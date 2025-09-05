# Spring Boot User Management System

> 基於 Spring Boot 3.x 的企業級用戶管理系統學習專案

## 專案概述

這是一個現代化的用戶管理系統，採用企業級標準設計，主要用於學習 Spring Boot 架構設計、容器化部署和 Kubernetes 環境。專案實現完整的 RBAC (Role-Based Access Control) 權限管理系統。

## 技術棧

- **框架**: Spring Boot 3.5.5 + Java 17
- **安全**: Spring Security + OAuth2 Resource Server
- **資料存取**: Spring Data JPA + Oracle XE 21c
- **資料庫版控**: Flyway Core
- **監控**: p6spy (SQL 監控) + Spring Boot Actuator
- **文檔**: SpringDoc OpenAPI 3
- **容器化**: Docker + Docker Compose

## 目前進度

### Phase 1: 基礎架構建立 ✅ (已完成)

#### Week 1-2: Maven + 基礎配置
- [x] Maven 專案初始化，包含所有核心依賴
- [x] 多環境配置檔案 (dev/sit/uat/prod)
- [x] JSON 結構化日誌配置
- [x] OAuth2 Resource Server 配置
- [x] Oracle XE 21c 容器化環境
- [x] Flyway 資料庫版本控制
- [x] 完整的 RBAC 資料庫架構
- [x] Spring Security 基本配置
- [x] SpringDoc OpenAPI 文檔整合

### Phase 2: 核心功能開發 🔄 (進行中)

#### Week 3: JPA 監控與基礎 CRUD
- [ ] p6spy 配置與 SQL 監控
- [ ] 基礎 Entity 實體類別
- [ ] Repository 介面實作
- [ ] 簡單查詢效能分析
- [ ] 用戶 CRUD API 實作

#### Week 4: N+1 問題學習
- [ ] 識別 N+1 問題案例
- [ ] @EntityGraph 解決方案
- [ ] JOIN FETCH 查詢優化
- [ ] 角色權限關聯查詢優化

## 資料模型

### 核心實體關係
```
User (用戶)                    Role (角色)                Permission (權限)
├── id                        ├── id                     ├── id
├── username                  ├── roleName               ├── permissionName  
├── email                     ├── description            ├── resource
├── password                  ├── createTime             └── description
├── createTime                ├── updateTime
├── updateTime                └── isActive
├── isActive
└── roles (多對多)             └── permissions (多對多)

        User ↔ UserRole ↔ Role ↔ RolePermission ↔ Permission
```

## 快速開始

### 系統需求
- Java 17+
- Maven 3.9+
- Docker 20.10+
- 16GB+ RAM (建議)

### 啟動步驟

1. **Clone 專案**
```bash
git clone https://github.com/your-username/spring-boot-user-management-system.git
cd spring-boot-user-management-system
```

2. **啟動資料庫**
```bash
docker-compose -f docker-compose-dev.yml up -d
```

3. **啟動應用程式**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

4. **驗證啟動**
- API 文檔: http://localhost:8080/api/swagger-ui.html
- 健康檢查: http://localhost:8080/api/actuator/health

### 停止服務
```bash
# 停止應用程式: Ctrl+C

# 停止資料庫
docker-compose -f docker-compose-dev.yml down
```

## API 端點

### 認證相關
```
POST /api/auth/register        # 用戶註冊
POST /api/auth/login           # 用戶登入
POST /api/auth/refresh         # 刷新 Token
```

### 用戶管理
```
GET  /api/users/list           # 用戶列表  
GET  /api/users/{id}           # 用戶詳情
POST /api/users/create         # 建立用戶
POST /api/users/update         # 更新用戶  
POST /api/users/disable        # 停用用戶
POST /api/users/enable         # 啟用用戶
```

### 角色權限
```
GET  /api/roles/list           # 角色列表
POST /api/users/{id}/assign-role   # 分配角色
POST /api/users/{id}/revoke-role   # 撤銷角色
```

## 環境配置

- **dev**: 開發環境 (本地 Oracle XE)
- **sit**: 系統整合測試環境
- **uat**: 用戶驗收測試環境
- **prod**: 生產環境

## 專案結構

```
spring-boot-user-management-system/
├── src/main/java/com/userms/
│   ├── SpringBootUserManagementApplication.java
│   ├── config/           # 配置類
│   ├── controller/       # REST API 控制器
│   ├── service/         # 業務邏輯層
│   ├── repository/      # 資料存取層
│   ├── entity/          # JPA 實體
│   ├── dto/             # 資料傳輸對象
│   └── exception/       # 全域異常處理
├── src/main/resources/
│   ├── application.yml  # 主配置檔案
│   ├── application-*.yml # 環境配置
│   └── db/migration/    # Flyway 遷移腳本
├── docker/              # Docker 相關檔案
└── docker-compose-dev.yml
```

## 開發指南

### 程式碼規範
- 使用 Java 17 語法特性
- 遵循 Spring Boot 最佳實踐
- 不使用 `var` 關鍵字（專案偏好）
- 繁體中文註解和文檔

### 資料庫操作
```bash
# 連線到開發資料庫
docker exec -it userms-oracle-xe sqlplus userms_dev/DevPassword123@localhost:1521/XEPDB1

# 查看資料表
SQL> SELECT table_name FROM user_tables ORDER BY table_name;

# 查看預設角色
SQL> SELECT role_name, role_code FROM roles ORDER BY display_order;
```

### 日誌監控
- 開發環境啟用 SQL 日誌
- 可選啟用 p6spy 詳細監控
- Actuator 提供健康檢查和指標

## 學習重點

### 核心技術
- OAuth2 Resource Server 自動 JWT 驗證
- JPA 效能調優和 N+1 問題解決
- Flyway 資料庫版本控制
- 全域異常處理標準化
- 容器化部署最佳實踐

### 進階主題
- 微服務架構準備
- CI/CD 自動化部署
- Kubernetes 容器編排
- 企業級監控和日誌管理

## 疑難排解

### 常見問題

**Q: 啟動時 Oracle 連線失敗？**
A: 確認 Docker 容器狀態：`docker-compose -f docker-compose-dev.yml ps`

**Q: Flyway 遷移失敗？**
A: 檢查資料庫用戶權限，確認初始化腳本執行成功

**Q: Swagger UI 顯示 401？**
A: 檢查 SecurityConfig 是否正確配置端點白名單

## 參考資源

- [Spring Boot 官方文檔](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/oauth2/resource-server/index.html)
- [Flyway 文檔](https://flywaydb.org/documentation/)
- [Oracle 21c 官方文檔](https://docs.oracle.com/en/database/oracle/oracle-database/21/)

## 授權

MIT License

## 貢獻

這是個人學習專案，歡迎 fork 和學習參考。如有問題或建議，請開 issue 討論。

---

**開發者**: [你的名字]  
**最後更新**: 2025-09-05