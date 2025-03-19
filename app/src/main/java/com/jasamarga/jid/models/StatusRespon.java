package com.jasamarga.jid.models;

import com.google.gson.JsonArray;

public class StatusRespon {
    private String msg;
    private Integer status;
    private JsonArray data;

    public StatusRespon(String msg, Integer status, JsonArray data) {
        this.msg = msg;
        this.status = status;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getStatus() {
        return status;
    }

    public JsonArray getData() {
        return data;
    }
}
