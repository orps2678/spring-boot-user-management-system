CREATE TABLE roles (
    id              VARCHAR2(36)        NOT NULL,
    role_name       VARCHAR2(50)        NOT NULL,
    role_code       VARCHAR2(30)        NOT NULL,
    description     VARCHAR2(200),
    is_active       NUMBER(1)           DEFAULT 1 NOT NULL,
    created_time    TIMESTAMP           NOT NULL,
    created_ts      NUMBER(19)          NOT NULL,
    updated_time    TIMESTAMP           NOT NULL,
    updated_ts      NUMBER(19)          NOT NULL,
    create_user     VARCHAR2(36),
    update_user     VARCHAR2(36),
    version         NUMBER(19)          DEFAULT 0 NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (role_name),
    CONSTRAINT uk_roles_code UNIQUE (role_code),
    CONSTRAINT ck_roles_is_active CHECK (is_active IN (0, 1))
);

-- 欄位中文註解
COMMENT ON COLUMN roles.id IS '角色唯一識別碼（UUID）';
COMMENT ON COLUMN roles.role_name IS '角色名稱';
COMMENT ON COLUMN roles.role_code IS '角色代碼（英文代號）';
COMMENT ON COLUMN roles.description IS '角色描述';
COMMENT ON COLUMN roles.is_active IS '是否啟用（1=啟用，0=停用）';
COMMENT ON COLUMN roles.created_time IS '建立時間（時間格式）';
COMMENT ON COLUMN roles.created_ts IS '建立時間（毫秒數）';
COMMENT ON COLUMN roles.updated_time IS '最後更新時間（時間格式）';
COMMENT ON COLUMN roles.updated_ts IS '最後更新時間（毫秒數）';
COMMENT ON COLUMN roles.create_user IS '建立者 UUID';
COMMENT ON COLUMN roles.update_user IS '最後修改者 UUID';
COMMENT ON COLUMN roles.version IS '版本號（用於樂觀鎖）';