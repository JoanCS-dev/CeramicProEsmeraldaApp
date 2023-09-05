package com.esmeralda.ceramicpro.model;

public class LastAppointmentVM {
    public long quotesID;
    public String quotesDate;
    public String quotesHour;
    public String serviceDesc;
    public String colorName;
    public String quotesSTS;
    public long vehicleModelID;
    public String vehicleModelName;
    public long vehicleBrandID;
    public String vehicleBrandName;

    public LastAppointmentVM(long quotesID, String quotesDate, String quotesHour, String serviceDesc, String colorName, String quotesSTS, long vehicleModelID, String vehicleModelName, long vehicleBrandID, String vehicleBrandName) {
        this.quotesID = quotesID;
        this.quotesDate = quotesDate;
        this.quotesHour = quotesHour;
        this.serviceDesc = serviceDesc;
        this.colorName = colorName;
        this.quotesSTS = quotesSTS;
        this.vehicleModelID = vehicleModelID;
        this.vehicleModelName = vehicleModelName;
        this.vehicleBrandID = vehicleBrandID;
        this.vehicleBrandName = vehicleBrandName;
    }

    public LastAppointmentVM() {
    }

    public String getQuotesDate() {
        return quotesDate;
    }

    public void setQuotesDate(String quotesDate) {
        this.quotesDate = quotesDate;
    }

    public String getQuotesHour() {
        return quotesHour;
    }

    public void setQuotesHour(String quotesHour) {
        this.quotesHour = quotesHour;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getQuotesSTS() {
        return quotesSTS;
    }

    public void setQuotesSTS(String quotesSTS) {
        this.quotesSTS = quotesSTS;
    }

    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public void setVehicleModelName(String vehicleModelName) {
        this.vehicleModelName = vehicleModelName;
    }

    public String getVehicleBrandName() {
        return vehicleBrandName;
    }

    public void setVehicleBrandName(String vehicleBrandName) {
        this.vehicleBrandName = vehicleBrandName;
    }
}
