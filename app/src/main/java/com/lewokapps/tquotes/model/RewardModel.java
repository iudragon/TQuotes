package com.lewokapps.tquotes.model;

import java.util.Date;

public class RewardModel {

    private String type;
    private String lowerLimit;
    private String upperLimit;
    private String discORamt;
    private String couponBody;
    private String couponTitleReward;
    private Date timestamp;
    private Boolean alreadyUsed;
    private String couponId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getDiscORamt() {
        return discORamt;
    }

    public void setDiscORamt(String discORamt) {
        this.discORamt = discORamt;
    }

    public String getCouponBody() {
        return couponBody.replace("<br>", "\n");
    }

    public void setCouponBody(String couponBody) {
        this.couponBody = couponBody;
    }

    public String getCouponTitleReward() {
        return couponTitleReward;
    }

    public void setCouponTitleReward(String couponTitleReward) {
        this.couponTitleReward = couponTitleReward;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(Boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public RewardModel(String couponId, String type, String lowerLimit, String upperLimit, String discORamt, String couponBody, String couponTitleReward, Date timestamp, Boolean alreadyUsed) {
        this.type = type;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.discORamt = discORamt;
        this.couponBody = couponBody;
        this.couponTitleReward = couponTitleReward;
        this.timestamp = timestamp;
        this.alreadyUsed = alreadyUsed;
        this.couponId = couponId;
    }
}
