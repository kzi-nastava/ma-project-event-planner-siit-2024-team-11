package com.example.eventy.users.model;

public class BlockUser {
    private Long blockedId;
    private Long blockerId;

    public BlockUser() {}

    public BlockUser(Long blockedId, Long blockerId) {
        this.blockedId = blockedId;
        this.blockerId = blockerId;
    }

    public Long getBlockedId() {
        return blockedId;
    }

    public void setBlockedId(Long blockedId) {
        this.blockedId = blockedId;
    }

    public Long getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(Long blockerId) {
        this.blockerId = blockerId;
    }
}
