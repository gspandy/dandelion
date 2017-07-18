package tsai.ewing.boot.entity;

import tsai.ewing.dandelion.annotation.Identity;
import tsai.ewing.dandelion.annotation.SqlName;

/**
 * 团队实体ID。
 **/
@SqlName("TEAM")
public class TeamId {
    @Identity
    private String myId;

    @Identity
    private String yourId;

    @Identity
    private String hisId;

    public TeamId() {
    }

    public TeamId(String myId, String yourId, String hisId) {
        this.myId = myId;
        this.yourId = yourId;
        this.hisId = hisId;
    }

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

}
