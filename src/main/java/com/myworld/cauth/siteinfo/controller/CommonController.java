package com.myworld.cauth.siteinfo.controller;

import com.myworld.cauth.siteinfo.entity.Writing;
import com.myworld.cauth.siteinfo.service.FileServiceImpl;
import com.myworld.cauth.siteinfo.service.WritingServiceImpl;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.FileUploadResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class CommonController {
    private static Logger log = LogManager.getRootLogger();
    private WritingServiceImpl writingService;
    private FileServiceImpl fileService;

    @Autowired
    public CommonController(WritingServiceImpl writingService, FileServiceImpl fileService) {
        this.writingService = writingService;
        this.fileService = fileService;
    }

    @GetMapping("/adminGetWritingList")
    public ApiResult adminGetWritingList(@RequestParam String type) {
        return this.writingService.adminGetWritingList(type);
    }

    @GetMapping("/adminCreateWriting")
    public ApiResult adminCreateWriting(@RequestParam String type, @RequestParam String title) {
        return this.writingService.adminCreateWriting(type, title);
    }
    @GetMapping("/adminGetWriting")
    public ApiResult adminGetWriting(@RequestParam String idDetail) {
        return writingService.adminGetWriting(idDetail);
    }

    @PostMapping("/adminSetWriting")
    public ApiResult adminSetWriting(@RequestBody Writing writing) {
        return writingService.adminSetWriting(writing);
    }

    @DeleteMapping("/adminDelWriting")
    public ApiResult adminDelWriting(@RequestParam String idDetail) {
        return writingService.adminDelWriting(idDetail);
    }



    @PostMapping("/adminUploadSmallPic")
    public ApiResult adminUploadSmallPic(@RequestParam String idDetail, @RequestParam MultipartFile file) throws IOException{
        return fileService.adminUploadSmallPic(idDetail, file);
    }

    @PostMapping("/adminUploadSmallPicBase64")
    public ApiResult adminUploadSmallPicBase64(@RequestParam String idDetail, @RequestBody String base64) {
        return fileService.adminUploadSmallPicBase64(idDetail, base64);
    }

    @PostMapping("/adminUploadTinymce/{fileUsage}/{idDetail}")
    public FileUploadResult adminUploadTinymce(@PathVariable String fileUsage, @PathVariable String idDetail, @RequestParam MultipartFile file) throws IOException {
        return fileService.adminUploadTinymce(fileUsage, idDetail, file);
    }


    @GetMapping("/adminGetFileList")
    public ApiResult adminGetFileList(){
        return fileService.adminGetFileList();
    }

    @PostMapping("/adminUploadFile")
    public ApiResult adminUploadFile(@RequestParam MultipartFile file, @RequestParam String officialName, @RequestParam String versionNumber) throws IOException {
        return fileService.adminUploadFile(file, officialName, versionNumber);
    }

    @DeleteMapping("/adminDeleteFile/{officialName}")
    public ApiResult adminDeleteFile(@PathVariable String officialName){
        return fileService.adminDeleteFile(officialName);
    }



    @GetMapping("/adminGetLinkList")
    public ApiResult adminGetLinkList(){
        return fileService.adminGetLinkList();
    }

    @PostMapping("/adminSetLink")
    public ApiResult adminSetLink(@RequestParam String officialName, @RequestParam String memo, @RequestBody String linkContent) {
        return fileService.adminSetLink(officialName, memo, linkContent);
    }

    @DeleteMapping("/adminDelLink")
    public ApiResult adminDelLink(@RequestParam String officialName) {
        return fileService.adminDelLink(officialName);
    }



    /**
     * 以下三个获取图片或者文件，都是通过gateway使用feign转发过来请求
     */
    @GetMapping("/getTinymcePhotoLocation/{idDetail}")
    public void getTinymcePhotoLocation(@PathVariable String idDetail, HttpServletResponse response) throws IOException {
        fileService.getTinymcePhotoLocation(idDetail, response);
    }

    @GetMapping("/getSmallPicLocation/{idDetail}")
    public void getSmallPicLocation(@PathVariable String idDetail, HttpServletResponse response) throws IOException {
        fileService.getSmallPicLocation(idDetail, response);
    }

    @GetMapping("/downloadFile/{officialName}")
    public void getMyFile(@PathVariable String officialName, HttpServletResponse response) throws IOException{
        fileService.getMyFile(officialName, response);
    }



    @GetMapping("/getSiteInfo")
    public ApiResult getSiteInfo(){
        return writingService.getSiteInfo();
    }

    @GetMapping("/getSiteInfoReleaseTime")
    public ApiResult getSiteInfoReleaseTime(@RequestParam String title) {
        return writingService.getSiteInfoReleaseTime(title);
    }

    @GetMapping("/getMyLinkAPK")
    public ApiResult getMyLinkAPK(){
        return fileService.getMyLinkAPK();
    }

    @GetMapping("/getAppInfoOnline")
    public ApiResult getAppInfoOnline() {
        return fileService.getAppInfoOnline();
    }
}
