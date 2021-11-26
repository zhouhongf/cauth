package com.myworld.cauth.siteinfo.entity;

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
@Table(name = "smallpic")
public class SmallPic implements Serializable {

    private String fileName;
    private String extensionType;
    @Column(name = "file_byte", columnDefinition = "LONGBLOB COMMENT '文件格式'")
    private byte[] fileByte;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time",columnDefinition="DATETIME COMMENT '创建时间'")
    private Timestamp createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time",columnDefinition="DATETIME COMMENT '最后更新时间'")
    private Timestamp updateTime;

    /**
     * 创建人账号username
     */
    private String creator;

    /**
     * 查找专用序列号，即writings的idDetail，两者相同
     */
    private String idDetail;


    public SmallPic() {
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
}
