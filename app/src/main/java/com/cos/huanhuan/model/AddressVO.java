package com.cos.huanhuan.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/23.
 */

public class AddressVO implements Serializable{

    private int id;//地址id
    private int userId;//用户id
    private String province;//省份
    private String city;//地级市
    private String county;//市、县级市
    private String address;//详细地址
    private String zipCode;//邮编
    private String name;//真实姓名
    private String phone;//手机号
    private Boolean isDefault;//是否默认
    private Boolean isManage;//是否点击了管理

    public Boolean getManage() {
        return isManage;
    }

    public void setManage(Boolean manage) {
        isManage = manage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
