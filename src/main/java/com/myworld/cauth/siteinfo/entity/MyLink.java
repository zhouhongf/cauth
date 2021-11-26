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
@Table(name = "mylink")
public class MyLink implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 查找专用序列号
     */
    private String idDetail;


    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private Timestamp createTime;

    /**
     * 创建人账号username
     */
    private String creator;



    /**
     * 官方名称
     */
    private String officialName;

    /**
     * 链接地址
     */
    @Column(columnDefinition="MEDIUMTEXT")
    private String linkContent;

    /**
     * 备注内容
     */
    @Column(columnDefinition="MEDIUMTEXT")
    private String memo;


    public MyLink() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getLinkContent() {
        return linkContent;
    }

    public void setLinkContent(String linkContent) {
        this.linkContent = linkContent;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
