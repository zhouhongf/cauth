package com.myworld.cauth.siteinfo.service;

import com.myworld.cauth.common.model.FileUploadResult;
import com.myworld.cauth.common.model.ApiResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface FileService {

    ByteArrayOutputStream makePhotoWithWatermark(ByteArrayInputStream byteArrayInputStream) throws IOException;

    ApiResult adminUploadSmallPic(String idDetail, MultipartFile file) throws IOException;
    ApiResult adminUploadSmallPicBase64(String idDetail, String base64);
    ApiResult adminSaveSmallPic(String idDetail, byte[] fileBytes, String fileName, String extensionType);
    FileUploadResult adminUploadTinymce(String fileUsage, String idDetail, MultipartFile file) throws IOException;

    ApiResult adminGetFileList();
    ApiResult adminUploadFile(MultipartFile file, String officialName, String versionNumber) throws IOException;
    ApiResult adminDeleteFile(String officialName);

    ApiResult adminGetLinkList();
    ApiResult adminSetLink(String officialName, String memo, String linkContent);
    ApiResult adminDelLink(String officialName);


    // 以下为feign传过来的需求
    void getTinymcePhotoLocation(String idDetail, HttpServletResponse response) throws IOException;
    void getSmallPicLocation(String idDetail, HttpServletResponse response) throws IOException;
    void getMyFile(String officialName, HttpServletResponse response) throws IOException;
    ApiResult getMyLinkAPK();
    ApiResult getAppInfoOnline();



}
