package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/8/23.
 */

public class ConfirmDetail {

    private int addressId;//地址id
    private String consignee;//收货人
    private String phoneMob;//移动电话
    private String address;//收货地址
    private String examine;//兑换方式
    private Double fare;//运费
    private Double deposit;//押金、保证金
    private Double price;//价格, 身家兑换时为最终值
    private Double priceSum;//总价

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhoneMob() {
        return phoneMob;
    }

    public void setPhoneMob(String phoneMob) {
        this.phoneMob = phoneMob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExamine() {
        return examine;
    }

    public void setExamine(String examine) {
        this.examine = examine;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(Double priceSum) {
        priceSum = priceSum;
    }
}
