CREATE TABLE permissions (
    id VARCHAR2(36) NOT NULL,
    permission_name VARCHAR2(100) NOT NULL,
    permission_code VARCHAR2(50) NOT NULL,
    resource_name VARCHAR2(100) NOT NULL,
    action_type VARCHAR2(50) NOT NULL,
    is_active NUMBER(1) DEFAULT 1 NOT NULL,
    created_time TIMESTAMP NOT NULL,
    created_ts NUMBER(19) NOT NULL,
    updated_time TIMESTAMP NOT NULL,
    updated_ts NUMBER(19) NOT NULL,
    create_user VARCHAR2(36),
    update_user VARCHAR2(36),
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uk_permissions_code UNIQUE (permission_code),
    CONSTRAINT ck_permissions_is_active CHECK (is_active IN (0, 1))
);

-- 欄位中文註解
COMMENT ON COLUMN permissions.id IS '權限唯一識別碼（UUID）';
COMMENT ON COLUMN permissions.permission_name IS '權限名稱';
COMMENT ON COLUMN permissions.permission_code IS '權限代碼';
COMMENT ON COLUMN permissions.resource_name IS '資源名稱（例如：USER）';
COMMENT ON COLUMN permissions.action_type IS '操作類型（例如：VIEW, MANAGE）';
COMMENT ON COLUMN permissions.is_active IS '是否啟用（1=啟用，0=停用）';
COMMENT ON COLUMN permissions.created_time IS '建立時間（時間格式）';
COMMENT ON COLUMN permissions.created_ts IS '建立時間（毫秒數）';
COMMENT ON COLUMN permissions.updated_time IS '最後更新時間（時間格式）';
COMMENT ON COLUMN permissions.updated_ts IS '最後更新時間（毫秒數）';
COMMENT ON COLUMN permissions.create_user IS '建立者 UUID';
COMMENT ON COLUMN permissions.update_user IS '最後修改者 UUID';
COMMENT ON COLUMN permissions.version IS '版本號（用於樂觀鎖）';