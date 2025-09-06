package com.userms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public class BaseEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    @EqualsAndHashCode.Include
    private String id;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_time", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Column(name = "created_ts", nullable = false, updatable = false)
    @JsonIgnore
    private Long createdTs;

    @Column(name = "updated_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @Column(name = "updated_ts", nullable = false)
    @JsonIgnore
    private Long updatedTs;

    @Column(name = "create_user", length = 36, updatable = false)
    private String createUser;

    @Column(name = "update_user", length = 36)
    private String updateUser;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private Long version = 0L;

    /**
     * 帶 ID 的建構子
     */
    public BaseEntity(String id) {
        this.id = StringUtils.isBlank(id) ? generateId() : id;
        this.isActive = true;
        this.version = 0L;
    }

    /**
     * 在保存前的處理
     * - 自動生成 UUID（如果為空）
     * - 設定時間戳記
     */
    @PrePersist
    protected void onCreate() {
        log.debug("@PrePersist called for entity: {}", this.getClass().getSimpleName());
        
        if (StringUtils.isBlank(this.id)) {
            this.id = generateId();
        }

        LocalDateTime now = LocalDateTime.now();
        long timestamp = System.currentTimeMillis();

        // 確保時間字段都被設置
        this.createdTime = now;
        this.createdTs = timestamp;
        this.updatedTime = now;
        this.updatedTs = timestamp;
        
        log.debug("Set timestamps: createdTs={}, updatedTs={}", this.createdTs, this.updatedTs);
    }

    /**
     * 在更新前的處理
     * - 自動更新時間戳記
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = LocalDateTime.now();
        this.updatedTs = System.currentTimeMillis();
    }

    /**
     * 生成 UUID
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 檢查實體是否為新實體（尚未保存）
     */
    public boolean isNew() {
        return StringUtils.isBlank(this.id) || this.version == null || this.version == 0L;
    }

    /**
     * 檢查實體是否已啟用
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * 檢查實體是否已停用
     */
    public boolean isInactive() {
        return !isActive();
    }

    /**
     * 啟用實體
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 停用實體（軟刪除）
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 切換啟用狀態
     */
    public void toggleActive() {
        this.isActive = !Boolean.TRUE.equals(this.isActive);
    }

    /**
     * 設定建立者資訊
     */
    public void setCreatedBy(String userId) {
        if (StringUtils.isNotBlank(userId)) {
            this.createUser = userId;
        }
    }

    /**
     * 設定更新者資訊
     */
    public void setUpdatedBy(String userId) {
        if (StringUtils.isNotBlank(userId)) {
            this.updateUser = userId;
        }
    }

    /**
     * 初始化新實體的審計資訊
     */
    public void initAuditInfo(String currentUserId) {
        if (isNew()) {
            setCreatedBy(currentUserId);
        }
        setUpdatedBy(currentUserId);
    }

    /**
     * 檢查是否為指定使用者建立
     */
    public boolean isCreatedBy(String userId) {
        return Objects.equals(this.createUser, userId);
    }

    /**
     * 檢查是否為指定使用者最後修改
     */
    public boolean isLastUpdatedBy(String userId) {
        return Objects.equals(this.updateUser, userId);
    }

    /**
     * 取得建立經過的時間（毫秒）
     */
    public long getCreatedElapsedTime() {
        return this.createdTs != null ? System.currentTimeMillis() - this.createdTs : 0L;
    }

    /**
     * 取得最後更新經過的時間（毫秒）
     */
    public long getUpdatedElapsedTime() {
        return this.updatedTs != null ? System.currentTimeMillis() - this.updatedTs : 0L;
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', isActive=%s, version=%d, createdTime=%s}",
                getClass().getSimpleName(),
                id,
                isActive,
                version,
                createdTime);
    }
}
