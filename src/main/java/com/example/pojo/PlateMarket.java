package com.example.pojo;

import java.io.Serializable;

public class PlateMarket implements Serializable {
    private String plateCode;

    private String date;

    private String plateName;

    private String chagnePercent;

    private String turnoverRate;

    private String transAmt;

    private String amplitude;

    private static final long serialVersionUID = 1L;

    public String getPlateCode() {
        return plateCode;
    }

    public void setPlateCode(String plateCode) {
        this.plateCode = plateCode == null ? null : plateCode.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName == null ? null : plateName.trim();
    }

    public String getChagnePercent() {
        return chagnePercent;
    }

    public void setChagnePercent(String chagnePercent) {
        this.chagnePercent = chagnePercent == null ? null : chagnePercent.trim();
    }

    public String getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(String turnoverRate) {
        this.turnoverRate = turnoverRate == null ? null : turnoverRate.trim();
    }

    public String getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(String transAmt) {
        this.transAmt = transAmt == null ? null : transAmt.trim();
    }

    public String getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude = amplitude == null ? null : amplitude.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", plateCode=").append(plateCode);
        sb.append(", date=").append(date);
        sb.append(", plateName=").append(plateName);
        sb.append(", chagnePercent=").append(chagnePercent);
        sb.append(", turnoverRate=").append(turnoverRate);
        sb.append(", transAmt=").append(transAmt);
        sb.append(", amplitude=").append(amplitude);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}