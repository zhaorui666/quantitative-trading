package com.example.pojo;

import java.io.Serializable;

public class StcokMarket implements Serializable {
    private String code;

    private String date;

    private String lastprice;

    private String changepercent;

    private String transamt;

    private String amplitude;

    private String beginprice;

    private String highestprice;

    private String lowestprice;

    private String turnoverrate;

    private static final long serialVersionUID = 1L;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public String getLastprice() {
        return lastprice;
    }

    public void setLastprice(String lastprice) {
        this.lastprice = lastprice == null ? null : lastprice.trim();
    }

    public String getChangepercent() {
        return changepercent;
    }

    public void setChangepercent(String changepercent) {
        this.changepercent = changepercent == null ? null : changepercent.trim();
    }

    public String getTransamt() {
        return transamt;
    }

    public void setTransamt(String transamt) {
        this.transamt = transamt == null ? null : transamt.trim();
    }

    public String getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(String amplitude) {
        this.amplitude = amplitude == null ? null : amplitude.trim();
    }

    public String getBeginprice() {
        return beginprice;
    }

    public void setBeginprice(String beginprice) {
        this.beginprice = beginprice == null ? null : beginprice.trim();
    }

    public String getHighestprice() {
        return highestprice;
    }

    public void setHighestprice(String highestprice) {
        this.highestprice = highestprice == null ? null : highestprice.trim();
    }

    public String getLowestprice() {
        return lowestprice;
    }

    public void setLowestprice(String lowestprice) {
        this.lowestprice = lowestprice == null ? null : lowestprice.trim();
    }

    public String getTurnoverrate() {
        return turnoverrate;
    }

    public void setTurnoverrate(String turnoverrate) {
        this.turnoverrate = turnoverrate == null ? null : turnoverrate.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", code=").append(code);
        sb.append(", date=").append(date);
        sb.append(", lastprice=").append(lastprice);
        sb.append(", changepercent=").append(changepercent);
        sb.append(", transamt=").append(transamt);
        sb.append(", amplitude=").append(amplitude);
        sb.append(", beginprice=").append(beginprice);
        sb.append(", highestprice=").append(highestprice);
        sb.append(", lowestprice=").append(lowestprice);
        sb.append(", turnoverrate=").append(turnoverrate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}