package com.example.pojo;

import java.io.Serializable;

public class StockBaseInfo implements Serializable {
    private String code;

    private String name;

    private String marketvalue;

    private String pe;

    private static final long serialVersionUID = 1L;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getMarketvalue() {
        return marketvalue;
    }

    public void setMarketvalue(String marketvalue) {
        this.marketvalue = marketvalue == null ? null : marketvalue.trim();
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe == null ? null : pe.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", code=").append(code);
        sb.append(", name=").append(name);
        sb.append(", marketvalue=").append(marketvalue);
        sb.append(", pe=").append(pe);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}