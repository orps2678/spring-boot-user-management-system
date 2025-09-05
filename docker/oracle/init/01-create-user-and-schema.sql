-- ============================================
-- Oracle XE 21c 用戶管理系統資料庫初始化腳本
-- 適用於多租戶架構 (CDB/PDB)
-- ============================================

-- 連線到 PDB (Pluggable Database)
ALTER SESSION SET CONTAINER = XEPDB1;

-- 建立開發環境用戶
CREATE USER userms_dev IDENTIFIED BY DevPassword123
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON USERS;

-- 授予必要權限
GRANT CONNECT, RESOURCE TO userms_dev;
GRANT CREATE SESSION TO userms_dev;
GRANT CREATE TABLE TO userms_dev;
GRANT CREATE SEQUENCE TO userms_dev;
GRANT CREATE VIEW TO userms_dev;
GRANT CREATE PROCEDURE TO userms_dev;
GRANT CREATE TRIGGER TO userms_dev;

-- 建立測試環境用戶
CREATE USER userms_sit IDENTIFIED BY SitPassword123
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON USERS;

-- 授予必要權限
GRANT CONNECT, RESOURCE TO userms_sit;
GRANT CREATE SESSION TO userms_sit;
GRANT CREATE TABLE TO userms_sit;
GRANT CREATE SEQUENCE TO userms_sit;
GRANT CREATE VIEW TO userms_sit;
GRANT CREATE PROCEDURE TO userms_sit;
GRANT CREATE TRIGGER TO userms_sit;

-- 建立 UAT 環境用戶
CREATE USER userms_uat IDENTIFIED BY UatPassword123
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON USERS;

-- 授予必要權限
GRANT CONNECT, RESOURCE TO userms_uat;
GRANT CREATE SESSION TO userms_uat;
GRANT CREATE TABLE TO userms_uat;
GRANT CREATE SEQUENCE TO userms_uat;
GRANT CREATE VIEW TO userms_uat;
GRANT CREATE PROCEDURE TO userms_uat;
GRANT CREATE TRIGGER TO userms_uat;

-- 驗證用戶建立
SELECT username, default_tablespace, temporary_tablespace, created
FROM dba_users
WHERE username IN ('USERMS_DEV', 'USERMS_SIT', 'USERMS_UAT');