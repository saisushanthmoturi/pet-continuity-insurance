package com.example.authservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("refresh_tokens")
public class RefreshToken {

    @Id
    @Column("token_id")
    private Long tokenId;

    @Column("user_id")
    private Long userId;

    @Column("token")
    private String token;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("status")
    private String status = "ACTIVE";

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public RefreshToken() {
    }

    public RefreshToken(Long tokenId, Long userId, String token, LocalDateTime expiresAt, String status, LocalDateTime createdAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static RefreshToken create(Long userId, String token, LocalDateTime expiresAt) {
        return new RefreshToken(null, userId, token, expiresAt, "ACTIVE", LocalDateTime.now());
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
