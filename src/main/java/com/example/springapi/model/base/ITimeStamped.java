package com.example.springapi.model.base;

import java.time.LocalDateTime;

public interface ITimeStamped {

    // Getter và Setter cho createdAt
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    // Getter và Setter cho updatedAt
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    // Getter và Setter cho deletedAt
    LocalDateTime getDeletedAt();
    void setDeletedAt(LocalDateTime deletedAt);

    /**
     * tự động cập nhật createdAt và updatedAt trước khi đối tượng được lưu vào DB.
     */
    default void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        setCreatedAt(now);
        setUpdatedAt(now);
    }

    /**
     * tự động cập nhật updatedAt trước khi đối tượng được cập nhật trong DB.
     */
    default void onUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

    /**
     * tự động đánh dấu thời gian deletedAt khi đối tượng bị xóa.
     */
    default void onDelete() {
        setDeletedAt(LocalDateTime.now());
    }
}

