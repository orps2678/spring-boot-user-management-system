-- 使用者與角色關聯表
CREATE TABLE user_roles (
    user_id     VARCHAR2(36) NOT NULL,
    role_id     VARCHAR2(36) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

COMMENT ON COLUMN user_roles.user_id IS '使用者 UUID';
COMMENT ON COLUMN user_roles.role_id IS '角色 UUID';

-- 角色與權限關聯表
CREATE TABLE role_permissions (
    role_id         VARCHAR2(36) NOT NULL,
    permission_id   VARCHAR2(36) NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

COMMENT ON COLUMN role_permissions.role_id IS '角色 UUID';
COMMENT ON COLUMN role_permissions.permission_id IS '權限 UUID';