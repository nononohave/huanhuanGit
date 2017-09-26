package com.cos.huanhuan.model;

/**
 * Created by Administrator on 2017/9/26.
 */

public class Logs {

    private String Terminal;//终端android|ios
    private String Model;//型号
    private String Version;//版本
    private String Rank;//级别
    private String Info;//信息

    public String getTerminal() {
        return Terminal;
    }

    public void setTerminal(String terminal) {
        Terminal = terminal;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }
}
