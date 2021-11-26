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
@Table(name = "tinymcephoto")
public class TinymcePhoto implements Serializable {


    private String fileName;
    private String extensionType;
    @Column(name = "file_byte", columnDefinition = "LONGBLOB COMMENT '文件格式'")
    private byte[] fileByte;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String idDetail;
    private String creator;

    @CreationTimestamp
    @Column(name = "create_time",columnDefinition="DATETIME COMMENT '创建时间'")
    private Timestamp createTime;
    private Timestamp updateTime;


    /**
     * 文件分类
     */
    private String fileUsage;
    private String usageIdDetail;

    /**
     * 是否使用NO, YES
     */
    private String onUse;


    public TinymcePhoto() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public void setFileByte(byte[] fileByte) {
        this.fileByte = fileByte;
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

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
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

    public String getFileUsage() {
        return fileUsage;
    }

    public void setFileUsage(String fileUsage) {
        this.fileUsage = fileUsage;
    }

    public String getUsageIdDetail() {
        return usageIdDetail;
    }

    public void setUsageIdDetail(String usageIdDetail) {
        this.usageIdDetail = usageIdDetail;
    }

    public String getOnUse() {
        return onUse;
    }

    public void setOnUse(String onUse) {
        this.onUse = onUse;
    }
}
