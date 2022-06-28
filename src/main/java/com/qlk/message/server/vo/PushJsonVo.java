package com.qlk.message.server.vo;

import com.alibaba.fastjson.JSON;

public class PushJsonVo {

    private APS aps;
    private Integer t = 0;// ios专用
    private Integer o = 0;
    // private Integer linktype;// 跳转类型 0,首页， 1，咨询列表，2咨询患者列表，3私人助理,4历史积分
    private String linkUrl;// 跳转类型
    private String extend;

    public APS getAps() {
        return aps;
    }

    public void setAps(APS aps) {
        this.aps = aps;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public Integer getO() {
        return o;
    }

    public void setO(Integer o) {
        this.o = o;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public static String toJSON(PushJsonVo vo) {
        return JSON.toJSONString(vo);
    }

    public static String defaultMessage(String message, String sound, String linkUrl) {
        PushJsonVo vo = new PushJsonVo();
        APS aps = new APS();
        aps.setAlert(message);
        aps.setSound(sound);
        aps.setTitle("");
        aps.setBadge(1);
        vo.setAps(aps);
        vo.setT(0);
        vo.o = 0;
        vo.setLinkUrl(linkUrl);
        vo.setExtend("");
        return JSON.toJSONString(vo);
    }

    public static void main(String[] args) {
        PushJsonVo vo = new PushJsonVo();
        APS aps = new APS();
        aps.setAlert("你倒是说话啊");
        aps.setSound("default");
        aps.setTitle("android标题");
        aps.setBadge(1);
        vo.setAps(aps);
        vo.setT(0);
        vo.setLinkUrl("qlkHome");
        vo.setExtend("创");
        System.out.println(PushJsonVo.toJSON(vo));
        System.out.println(PushJsonVo.defaultMessage("你倒是说话啊", "", null));
    }

}
