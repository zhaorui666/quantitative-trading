package com.zr.pojo;

import java.io.Serializable;

public class StockFinaSta implements Serializable {
    private String code;

    private String reportName;

    private String totalRevenue;

    private String totalRevenueInc;

    private String dedNonNetProfit;

    private String dedNonNetProfitInc;

    private String grossSellingRate;

    private String grossSellingRateInc;

    private String assetLiabRatio;

    private String assetLiabRatioInc;

    private String basicEps;

    private String basicEpsInc;

    private Integer date;

    private static final long serialVersionUID = 1L;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName == null ? null : reportName.trim();
    }

    public String getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(String totalRevenue) {
        this.totalRevenue = totalRevenue == null ? null : totalRevenue.trim();
    }

    public String getTotalRevenueInc() {
        return totalRevenueInc;
    }

    public void setTotalRevenueInc(String totalRevenueInc) {
        this.totalRevenueInc = totalRevenueInc == null ? null : totalRevenueInc.trim();
    }

    public String getDedNonNetProfit() {
        return dedNonNetProfit;
    }

    public void setDedNonNetProfit(String dedNonNetProfit) {
        this.dedNonNetProfit = dedNonNetProfit == null ? null : dedNonNetProfit.trim();
    }

    public String getDedNonNetProfitInc() {
        return dedNonNetProfitInc;
    }

    public void setDedNonNetProfitInc(String dedNonNetProfitInc) {
        this.dedNonNetProfitInc = dedNonNetProfitInc == null ? null : dedNonNetProfitInc.trim();
    }

    public String getGrossSellingRate() {
        return grossSellingRate;
    }

    public void setGrossSellingRate(String grossSellingRate) {
        this.grossSellingRate = grossSellingRate == null ? null : grossSellingRate.trim();
    }

    public String getGrossSellingRateInc() {
        return grossSellingRateInc;
    }

    public void setGrossSellingRateInc(String grossSellingRateInc) {
        this.grossSellingRateInc = grossSellingRateInc == null ? null : grossSellingRateInc.trim();
    }

    public String getAssetLiabRatio() {
        return assetLiabRatio;
    }

    public void setAssetLiabRatio(String assetLiabRatio) {
        this.assetLiabRatio = assetLiabRatio == null ? null : assetLiabRatio.trim();
    }

    public String getAssetLiabRatioInc() {
        return assetLiabRatioInc;
    }

    public void setAssetLiabRatioInc(String assetLiabRatioInc) {
        this.assetLiabRatioInc = assetLiabRatioInc == null ? null : assetLiabRatioInc.trim();
    }

    public String getBasicEps() {
        return basicEps;
    }

    public void setBasicEps(String basicEps) {
        this.basicEps = basicEps == null ? null : basicEps.trim();
    }

    public String getBasicEpsInc() {
        return basicEpsInc;
    }

    public void setBasicEpsInc(String basicEpsInc) {
        this.basicEpsInc = basicEpsInc == null ? null : basicEpsInc.trim();
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", code=").append(code);
        sb.append(", reportName=").append(reportName);
        sb.append(", totalRevenue=").append(totalRevenue);
        sb.append(", totalRevenueInc=").append(totalRevenueInc);
        sb.append(", dedNonNetProfit=").append(dedNonNetProfit);
        sb.append(", dedNonNetProfitInc=").append(dedNonNetProfitInc);
        sb.append(", grossSellingRate=").append(grossSellingRate);
        sb.append(", grossSellingRateInc=").append(grossSellingRateInc);
        sb.append(", assetLiabRatio=").append(assetLiabRatio);
        sb.append(", assetLiabRatioInc=").append(assetLiabRatioInc);
        sb.append(", basicEps=").append(basicEps);
        sb.append(", basicEpsInc=").append(basicEpsInc);
        sb.append(", date=").append(date);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}