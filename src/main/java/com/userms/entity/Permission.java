package com.userms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Permission extends BaseEntity {

    @Column(name = "permission_name", length = 100, nullable = false)
    @NotBlank(message = "權限名稱不能為空")
    @Size(min = 2, max = 100, message = "權限名稱長度必須在 2-100 字元之間")
    private String permissionName;

    @Column(name = "permission_code", length = 50, nullable = false, unique = true)
    @NotBlank(message = "權限代碼不能為空")
    @Size(min = 2, max = 50, message = "權限代碼長度必須在 2-50 字元之間")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "權限代碼必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String permissionCode;

    @Column(name = "resource_name", length = 100, nullable = false)
    @NotBlank(message = "資源名稱不能為空")
    @Size(min = 2, max = 100, message = "資源名稱長度必須在 2-100 字元之間")
    private String resourceName;

    @Column(name = "action_type", length = 50, nullable = false)
    @NotBlank(message = "操作類型不能為空")
    @Size(min = 2, max = 50, message = "操作類型長度必須在 2-50 字元之間")
    private String actionType;
}
