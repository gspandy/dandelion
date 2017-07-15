package com.ewing.boot.entity;

import com.ewing.dandelion.annotation.SqlName;
import com.ewing.dandelion.annotation.Temporary;

import java.util.Date;

/**
 * 团队实体，多ID。
 **/
@SqlName("TEAM")
public class Team extends TeamId {
    private String name;

    private String description;

    @Temporary
    private String temporary;

    private Date createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
