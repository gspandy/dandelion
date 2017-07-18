package ewing.boot.entity;

import ewing.dandelion.annotation.Identity;

/**
 * 用户实体类。
 */
public class User {
    @Identity
    private String userId;

    private String name;

    private Integer level;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
