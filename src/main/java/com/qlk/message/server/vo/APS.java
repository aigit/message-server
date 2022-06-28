package com.qlk.message.server.vo;

public class APS {
    
    private String alert;// 消息内容
    private String sound = "Default";// 消息声音 传空就是没声音和震动
    private String title = "";// 传空则使用默认标题。
    private int badge = 1;// ios专用(数量)
    public String getAlert() {
        return alert;
    }
    public void setAlert(String alert) {
        this.alert = alert;
    }
    public String getSound() {
        return sound;
    }
    public void setSound(String sound) {
        this.sound = sound;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getBadge() {
        return badge;
    }
    public void setBadge(int badge) {
        this.badge = badge;
    }
    
}
