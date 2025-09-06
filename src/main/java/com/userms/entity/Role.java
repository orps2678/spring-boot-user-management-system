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
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Role extends BaseEntity {

    @Column(name = "role_name", length = 50, nullable = false, unique = true)
    @NotBlank(message = "角色名稱不能為空")
    @Size(min = 2, max = 50, message = "角色名稱長度必須在 2-50 字元之間")
    private String roleName;

    @Column(name = "role_code", length = 30, nullable = false, unique = true)
    @NotBlank(message = "角色代碼不能為空")
    @Size(min = 2, max = 30, message = "角色代碼長度必須在 2-30 字元之間")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色代碼必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String roleCode;

    @Column(name = "description", length = 200)
    @Size(max = 200, message = "角色描述長度不能超過 200 字元")
    private String description;
}
