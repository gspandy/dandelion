package com.ewing.boot.genericdao.entity;

import com.ewing.dandelion.annotation.Identity;
import com.ewing.dandelion.annotation.SqlName;
import com.ewing.dandelion.annotation.Temporary;

import java.util.Date;

/**
 * 团队实体，多ID。
 **/
@SqlName("TEAM")
public class Team {
    @Identity
    private String myId;

    @Identity
    private String yourId;

    @Identity
    private String hisId;

    private String name;

    private String description;

    @Temporary
    private String temporary;

    private Date createTime;

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getYourId() {
        return yourId;
    }

    public void setYourId(String yourId) {
        this.yourId = yourId;
    }

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

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
