# Spring Boot 用戶管理系統 - 學習專案

## 📋 專案概述

這是一個基於 Spring Boot 的用戶管理系統 side project，主要目標是學習現代化的 Spring Boot 架構設計、容器化部署和 K8s 環境。專案採用企業級標準，包含完整的認證授權、權限管理和容器化部署流程。

## 🎯 學習目標

### 核心學習重點
- ⭐ **OAuth2 Resource Server** 自動 JWT 驗證機制
- ⭐ **JPA 效能調優** SQL 監控、N+1 問題解決、效能基準建立
- ⭐ **Flyway** 資料庫版本控制
- ⭐ **全域異常處理** 標準化錯誤處理
- ⭐ **容器化部署** Docker + minikube + K8s
- ⭐ **JSON 結構化日誌** K8s 友善的日誌格式

### 延伸學習
- RESTful API 標準設計 (包含 PUT/DELETE)
- CI/CD 自動化部署流程
- 微服務架構準備

## 🏗️ 技術架構

### 技術棧
- **框架**: Spring Boot 3.2.x + Java 17
- **安全**: Spring Security + OAuth2 Resource Server
- **資料存取**: Spring Data JPA + Oracle XE
- **資料庫版控**: Flyway Core
- **監控**: p6spy (SQL 監控) + Spring Boot Actuator
- **文檔**: SpringDoc OpenAPI 3
- **容器化**: Docker + minikube
- **CI/CD**: GitHub Actions → Docker Hub

### 架構設計
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │     Service     │    │   Repository    │
│   (REST API)    │───▶│  (業務邏輯)      │───▶│   (資料存取)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Global Exception│    │   JWT Security  │    │   Oracle XE +   │
│    Handler      │    │   + OAuth2 RS   │    │     Flyway      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 功能模組
- **認證模組**: 用戶註冊、登入、JWT Token 管理
- **用戶管理**: CRUD、狀態管理 (軟刪除)
- **權限管理**: RBAC 角色權限控制
- **系統管理**: 健康檢查、監控指標

## 📊 資料模型

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
             (多對多關係表)        (多對多關係表)
```

## 🌍 環境配置

### 多環境支援
- **dev**: 開發環境 (本地 Oracle XE)
- **sit**: 系統整合測試環境
- **uat**: 用戶驗收測試環境  
- **prod**: 生產環境

### 配置檔案結構
```
src/main/resources/
├── application.yml              # 公共配置
├── application-dev.yml          # 開發環境
├── application-sit.yml          # 測試環境
├── application-uat.yml          # 驗收環境
├── application-prod.yml         # 生產環境
└── db/migration/                # Flyway 遷移腳本
    ├── V1.0.0__Create_user_table.sql
    ├── V1.0.1__Create_role_table.sql
    └── V1.0.2__Create_permission_table.sql
```

## 🛠️ API 設計規範

### URL 命名規範
```
# 用戶管理
GET  /api/users/list           # 用戶列表  
GET  /api/users/{id}           # 用戶詳情
POST /api/users/create         # 建立用戶
POST /api/users/update         # 更新用戶  
POST /api/users/disable        # 停用用戶
POST /api/users/enable         # 啟用用戶

# 認證相關
POST /api/auth/register        # 用戶註冊
POST /api/auth/login           # 用戶登入
POST /api/auth/refresh         # 刷新 Token

# 角色權限
GET  /api/roles/list           # 角色列表
POST /api/users/{id}/assign-role   # 分配角色
POST /api/users/{id}/revoke-role   # 撤銷角色
```

### 統一回應格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "errorCode": null,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

### 錯誤處理格式 (RESTful + K8s 友善)
```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation Failed", 
  "status": 400,
  "detail": "The request body contains invalid fields",
  "instance": "/api/users/create",
  "timestamp": "2025-01-15T10:30:00Z",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ]
}
```

## 🚀 開發計劃

### Phase 1: 基礎架構建立 (Week 1-2)

#### Week 1: Maven + 基礎配置
- [ ] Maven 專案初始化，包含所有核心依賴
- [ ] 多環境配置檔案 (dev/sit/uat/prod)  
- [ ] JSON 結構化日誌配置
- [ ] 基礎包結構建立

#### Week 2: 安全框架 + 資料庫
- [ ] **OAuth2 Resource Server 配置** ⭐學習重點
- [ ] Oracle XE 連線配置
- [ ] **Flyway 初始化** ⭐學習重點  
- [ ] 基礎實體設計 (User, Role, Permission)

### Phase 2: 核心功能開發 (Week 3-6)

#### Week 3: JPA 監控與基礎 CRUD  
- [ ] **p6spy 配置與 SQL 監控** ⭐JPA學習: SQL執行監控
- [ ] 基礎 Repository 實作
- [ ] 簡單查詢效能分析
- [ ] 用戶 CRUD API 實作

#### Week 4: N+1 問題學習
- [ ] **識別 N+1 問題案例** ⭐JPA學習: N+1問題識別  
- [ ] **@EntityGraph 解決方案** ⭐JPA學習: N+1問題解決
- [ ] JOIN FETCH 查詢優化
- [ ] 角色權限關聯查詢優化

#### Week 5: 認證與權限
- [ ] JWT 登入註冊 API
- [ ] 權限驗證機制  
- [ ] **Global Exception Handler** ⭐學習重點
- [ ] API 文檔整合

#### Week 6: 效能基準與優化
- [ ] **查詢效能基準建立** ⭐JPA學習: 效能基準建立
- [ ] Actuator 監控指標
- [ ] 批次操作優化  
- [ ] Specification 動態查詢學習

### Phase 3: 容器化部署 (Week 7-8)

#### Week 7: Docker 環境
- [ ] docker-compose 本地開發環境
- [ ] Dockerfile 多階段建構  
- [ ] Oracle XE 容器化
- [ ] 環境變數配置

#### Week 8: K8s 部署  
- [ ] **遷移到全 minikube 環境** ⭐學習重點
- [ ] K8s 資源配置 (Deployment, Service, ConfigMap, Secret)
- [ ] 健康檢查配置
- [ ] 網路與存儲配置

### Phase 4: CI/CD 與完善 (Week 9-10)

#### Week 9: 自動化部署
- [ ] GitHub Actions 配置
- [ ] Docker Hub 自動推送
- [ ] 多環境部署流程

#### Week 10: 優化與總結  
- [ ] 效能調優總結
- [ ] 監控指標完善
- [ ] 文檔撰寫
- [ ] 學習成果整理

## 📚 JPA 學習重點詳解

### 1. SQL 執行監控 (Week 3)
**目標**: 學會監控和分析 JPA 生成的 SQL
- 配置 p6spy 監控工具
- 觀察 findById()、findAll() 等方法生成的 SQL
- 分析 SQL 執行時間和頻率
- 比較 JPQL vs 原生 SQL 的差異

### 2. N+1 問題識別與解決 (Week 4)  
**目標**: 識別並解決 JPA 最常見的效能問題
- **識別**: 透過 SQL 日誌發現 N+1 查詢模式
- **解決**: 學習 @EntityGraph、JOIN FETCH、@BatchSize
- **實務案例**: 用戶-角色關聯查詢優化
- **最佳實踐**: 預載入策略選擇

### 3. 查詢效能基準建立 (Week 6)
**目標**: 建立效能監控和優化標準
- 設定查詢時間警告閾值  
- 建立效能測試案例
- 監控記憶體使用情況
- 整合 Actuator 查看 JPA 統計資訊

## 🐳 容器化部署

### Docker 部署策略
```bash
# 開發階段: docker-compose
docker-compose up -d

# 生產階段: K8s
kubectl apply -f k8s/
```

### 部署架構演進
1. **本地開發**: docker-compose (Oracle XE + 應用程式)
2. **學習階段**: 全 minikube 環境  
3. **生產準備**: 雲端 K8s 部署

## 📈 監控與日誌

### 健康檢查端點
```
GET /actuator/health        # 應用健康狀態
GET /actuator/metrics       # 效能指標  
GET /actuator/info          # 應用資訊
```

### JSON 結構化日誌範例
```json
{
  "timestamp": "2025-01-15T10:30:00.123Z",
  "level": "INFO", 
  "logger": "com.userms.controller.UserController",
  "message": "User created successfully",
  "traceId": "abc123def456",
  "userId": "user123",
  "requestId": "req-456", 
  "endpoint": "POST /api/users/create"
}
```

## 🔧 開發環境設置

### 系統需求
- **Java**: OpenJDK 17+
- **Maven**: 3.9+  
- **Docker**: 20.10+
- **記憶體**: 16GB+ (建議)
- **CPU**: 4 核心+

### 快速啟動
```bash
# 1. Clone 專案
git clone <repository-url>
cd spring-boot-user-management

# 2. 啟動資料庫
docker-compose -f docker/docker-compose-dev.yml up -d

# 3. 執行應用程式  
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 4. 訪問 API 文檔
http://localhost:8080/swagger-ui.html
```

## 📖 學習資源

### 官方文檔
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/oauth2/resource-server/index.html)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

### 效能調優指南
- [JPA Performance Best Practices](https://vladmihalcea.com/jpa-hibernate-performance/)
- [Spring Boot Production Readiness](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 🎯 成功指標

### 技術指標
- [ ] 所有 API 響應時間 < 200ms (95th percentile)
- [ ] 零 N+1 查詢問題
- [ ] 單元測試覆蓋率 > 80%
- [ ] Docker image 大小 < 200MB
- [ ] K8s 部署成功率 100%

### 學習指標  
- [ ] 掌握 OAuth2 Resource Server 自動驗證機制
- [ ] 能夠識別和解決 JPA 效能問題
- [ ] 熟練使用 Flyway 管理資料庫版本
- [ ] 建立完整的容器化部署流程
- [ ] 實現標準化的錯誤處理和日誌記錄

## 🤝 貢獻指南

這是一個個人學習專案，歡迎 fork 和參考，但請注意：
- 這是學習用途的程式碼，不適合直接用於生產環境
- 如有問題或建議，歡迎開 issue 討論
- 請保持程式碼風格一致性

## 📝 版本記錄

- **v1.0.0**: 基礎架構完成，核心功能實作
- **v1.1.0**: 容器化部署完成
- **v1.2.0**: CI/CD 流程建立
- **v2.0.0**: 效能優化與監控完善

---

**Happy Coding! 🚀**
