package com.kachabazars.ecommerce;

public class DivisionModel
{
    private  String divisionId, divisionName, divisionBanglaName;

    public DivisionModel(String divisionId, String divisionName, String divisionBanglaName) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.divisionBanglaName = divisionBanglaName;
    }

    public DivisionModel() {

    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getDivisionBanglaName() {
        return divisionBanglaName;
    }

    public void setDivisionBanglaName(String divisionBanglaName) {
        this.divisionBanglaName = divisionBanglaName;
    }
}
