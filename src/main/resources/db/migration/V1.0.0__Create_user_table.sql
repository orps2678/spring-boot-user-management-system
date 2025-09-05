CREATE TABLE users (
    id              VARCHAR2(36)        NOT NULL,
    username        VARCHAR2(50)        NOT NULL,
    email           VARCHAR2(100)       NOT NULL,
    password_hash   VARCHAR2(255)       NOT NULL,
    first_name      VARCHAR2(50),
    last_name       VARCHAR2(50),
    is_active       NUMBER(1)           DEFAULT 1 NOT NULL,
    created_time    TIMESTAMP           NOT NULL,
    created_ts      NUMBER(19)          NOT NULL,
    updated_time    TIMESTAMP           NOT NULL,
    updated_ts      NUMBER(19)          NOT NULL,
    create_user     VARCHAR2(36),
    update_user     VARCHAR2(36),
    version         NUMBER(19)          DEFAULT 0 NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_is_active CHECK (is_active IN (0, 1))
);

-- 欄位中文註解
COMMENT ON COLUMN users.id IS '使用者唯一識別碼（UUID）';
COMMENT ON COLUMN users.username IS '使用者帳號';
COMMENT ON COLUMN users.email IS '電子郵件地址';
COMMENT ON COLUMN users.password_hash IS '密碼雜湊值';
COMMENT ON COLUMN users.first_name IS '使用者名字';
COMMENT ON COLUMN users.last_name IS '使用者姓氏';
COMMENT ON COLUMN users.is_active IS '是否啟用（1=啟用，0=停用）';
COMMENT ON COLUMN users.created_time IS '建立時間（時間格式）';
COMMENT ON COLUMN users.created_ts IS '建立時間（毫秒數）';
COMMENT ON COLUMN users.updated_time IS '最後更新時間（時間格式）';
COMMENT ON COLUMN users.updated_ts IS '最後更新時間（毫秒數）';
COMMENT ON COLUMN users.create_user IS '建立者 UUID';
COMMENT ON COLUMN users.update_user IS '最後修改者 UUID';
COMMENT ON COLUMN users.version IS '版本號（用於樂觀鎖）';