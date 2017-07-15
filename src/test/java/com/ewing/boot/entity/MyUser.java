package com.ewing.boot.entity;

import com.ewing.dandelion.annotation.Temporary;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 扩展的用户实体类。
 **/
public class MyUser extends User {
    @Temporary
    private String temporary;

    private Boolean boolValue;

    private Date dateValue;

    private BigDecimal bigDecimal;

    private Integer intValue;

    private Long longValue;

    private Float floatValue;

    private Double doubleValue;

    private Short shortValue;

    private byte[] bytesValue;

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public byte[] getBytesValue() {
        return bytesValue;
    }

    public void setBytesValue(byte[] bytesValue) {
        this.bytesValue = bytesValue;
    }
}
