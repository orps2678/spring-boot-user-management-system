package com.userms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"passwordHash"})
public class User extends BaseEntity {

    @Column(name = "username", length = 50, nullable = false, unique = true)
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 3, max = 50, message = "使用者名稱長度必須在 3-50 字元之間")
    private String username;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 100, message = "電子郵件長度不能超過 100 字元")
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    @NotBlank(message = "密碼不能為空")
    @JsonIgnore
    private String passwordHash;

    @Column(name = "first_name", length = 50)
    @Size(max = 50, message = "名字長度不能超過 50 字元")
    private String firstName;

    @Column(name = "last_name", length = 50)
    @Size(max = 50, message = "姓氏長度不能超過 50 字元")
    private String lastName;

    /**
     * 用戶角色關聯 - 用於 @EntityGraph 優化查詢
     * 解決 N+1 問題的關鍵關聯關係
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserRole> userRoles = new ArrayList<>();

    /**
     * 取得使用者的完整姓名
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return lastName + firstName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }

    /**
     * 檢查密碼是否已設定
     */
    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.trim().isEmpty();
    }

    /**
     * 獲取用戶的活躍角色列表
     * 用於 @EntityGraph 查詢後的角色獲取
     */
    @JsonIgnore
    public List<Role> getActiveRoles() {
        return userRoles.stream()
                .filter(ur -> ur.getRole().getIsActive())
                .map(UserRole::getRole)
                .toList();
    }

    /**
     * 獲取用戶的活躍角色代碼列表  
     * 用於 @EntityGraph 查詢後的角色代碼獲取
     */
    @JsonIgnore
    public List<String> getActiveRoleCodes() {
        return getActiveRoles().stream()
                .map(Role::getRoleCode)
                .toList();
    }
}
