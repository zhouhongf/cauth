package com.myworld.cauth.common.model;

import java.sql.Timestamp;

public class TitleContent {

    private Timestamp createTime;
    private String idDetail;

    private String title;
    private String content;

    private String alreadyRead;

    public TitleContent() {
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(String alreadyRead) {
        this.alreadyRead = alreadyRead;
    }
}
