package com.myworld.cauth.common.model;

public class FileUploadResult {

    private Integer code;
    private String filename;
    private String location;

    /**
     * 查找专用序号，根据project的idDetail查找
     */
    private String projectIdDetail;
    /**
     * 文章的idDetail
     */
    private String writingsIdDetail;
    /**
     * 该图片的idDetail
     */
    private String idDetail;


    public FileUploadResult() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProjectIdDetail() {
        return projectIdDetail;
    }

    public void setProjectIdDetail(String projectIdDetail) {
        this.projectIdDetail = projectIdDetail;
    }

    public String getWritingsIdDetail() {
        return writingsIdDetail;
    }

    public void setWritingsIdDetail(String writingsIdDetail) {
        this.writingsIdDetail = writingsIdDetail;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }
}
