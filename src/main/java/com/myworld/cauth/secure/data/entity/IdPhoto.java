package com.myworld.cauth.secure.data.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "idphoto")
public class IdPhoto implements Serializable {

    private String fileName;
    private String extensionType;
    @Column(name = "file_byte", columnDefinition = "LONGBLOB COMMENT '文件格式'")
    private byte[] fileByte;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "create_time",columnDefinition="DATETIME COMMENT '创建时间'")
    private Timestamp createTime;

    @UpdateTimestamp
    @Column(name = "update_time",columnDefinition="DATETIME COMMENT '更新时间'")
    private Timestamp updateTime;

    /**
     * 查找专用序列号，由creator加上system时间拼接而成
     */
    private String idDetail;
    /**
     * 创建人idDetail
     */
    private String creator;
    /**
     * 指定的上传名称
     */
    private String photoName;

    private String userIdDetail;

    public IdPhoto() {
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

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUserIdDetail() {
        return userIdDetail;
    }

    public void setUserIdDetail(String userIdDetail) {
        this.userIdDetail = userIdDetail;
    }
}

