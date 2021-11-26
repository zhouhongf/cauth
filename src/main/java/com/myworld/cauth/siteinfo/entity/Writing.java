package com.myworld.cauth.siteinfo.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "writing")
public class Writing implements Serializable {

    @CreationTimestamp
    @Column(name = "create_time",columnDefinition="DATETIME COMMENT '创建时间'")
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp releaseTime;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idDetail;
    private String creator;

    private String type;
    private String canRelease;

    private String title;
    private String shortInfo;
    private String author;
    @Column(name = "content",columnDefinition="LONGTEXT")
    private String content;
    private String smallPic;

    private Integer readNums;

    public Writing() {
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Timestamp releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCanRelease() {
        return canRelease;
    }

    public void setCanRelease(String canRelease) {
        this.canRelease = canRelease;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic;
    }

    public Integer getReadNums() {
        return readNums;
    }

    public void setReadNums(Integer readNums) {
        this.readNums = readNums;
    }
}
