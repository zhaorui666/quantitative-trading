package com.zr.pojo;

import java.io.Serializable;

public class PlateInfo implements Serializable {
    private String palteCode;

    private String palteName;

    private static final long serialVersionUID = 1L;

    public String getPalteCode() {
        return palteCode;
    }

    public void setPalteCode(String palteCode) {
        this.palteCode = palteCode == null ? null : palteCode.trim();
    }

    public String getPalteName() {
        return palteName;
    }

    public void setPalteName(String palteName) {
        this.palteName = palteName == null ? null : palteName.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", palteCode=").append(palteCode);
        sb.append(", palteName=").append(palteName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}