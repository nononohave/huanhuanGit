package com.cos.huanhuan.model;

import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class Tracking {

    private String shipperCode;//物流公司
    private String logisticCode;//物流单号
    private String state;//物流状态
    private String resultModel;//物流客服
    private List<TrackLine> accept;

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResultModel() {
        return resultModel;
    }

    public void setResultModel(String resultModel) {
        this.resultModel = resultModel;
    }

    public List<TrackLine> getAccept() {
        return accept;
    }

    public void setAccept(List<TrackLine> accept) {
        this.accept = accept;
    }
}
