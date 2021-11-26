package com.myworld.cauth.secure.controller;

import com.myworld.cauth.secure.service.UserInfoService;
import com.myworld.cauth.common.model.ApiResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UserInfoController {
    private static Logger log = LogManager.getRootLogger();
    private UserInfoService userInfoService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 用于获取 《用户信息》
     */
    @GetMapping("/getUserInfo")
    public ApiResult getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException{
        return userInfoService.getUserInfo(request, response);
    }
    /**
     * 用于更新 《用户信息》，之后用户账户状态变为“待认证”
     */
    @PostMapping("/setUserInfo")
    public ApiResult setUserInfo(@RequestBody String userInfoStr, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.setUserInfo(userInfoStr, request, response);
    }

    @GetMapping("/getUserInfoBasic")
    public ApiResult getUserInfoBasic(HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.getUserInfoBasic(request, response);
    }

    /**
     * 用于《实名认证》，之后用户账户状态变为“认证中”
     */
    @PostMapping("/setIdInfo")
    public ApiResult setIdInfo(@RequestBody String idnumber, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.setIdInfo(idnumber, request, response);
    }

    @GetMapping("/saveNickname")
    public ApiResult saveNickname(@RequestParam String nickname, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.saveNickname(nickname, request, response);
    }

    @GetMapping("/getIdCheckResultReason")
    public ApiResult getIdCheckResultReason(HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.getIdCheckResultReason(request, response);
    }

    @PostMapping("/uploadUserAvatarBase64")
    public ApiResult uploadUserAvatarBase64(@RequestBody String base64, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.uploadUserAvatarBase64(base64, request, response);
    }

    @GetMapping(value = "/avatar/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getAvatar(@PathVariable String id, HttpServletResponse response) throws IOException {
        userInfoService.getAvatar(id, response);
    }

    @PostMapping("/uploadIdPhotoBase64")
    public ApiResult uploadIdPhotoBase64(@RequestParam String photoName, @RequestBody String base64, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.uploadIdPhotoBase64(photoName, base64, request, response);
    }

    @PostMapping(value = "/uploadIdPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult uploadIdPhoto(@RequestParam String photoName, @RequestBody MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return userInfoService.uploadIdPhoto(photoName, file, request, response);
    }

    @GetMapping(value = "/getFileLocation/{idDetail}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFileLocation(@PathVariable String idDetail, HttpServletResponse response) throws IOException {
        userInfoService.getFileLocation(idDetail, response);
    }

    @GetMapping("/getBase64File/{idDetail}")
    public ApiResult getBase64File(@PathVariable String idDetail) {
        return userInfoService.getBase64File(idDetail);
    }



    /**
     * 《用户信息》，获取用户信息列表
     */
    @GetMapping("/adminGetUserInfoList")
    public ApiResult adminGetUserInfoList(@RequestParam String playerType, @RequestParam Integer pageSize, @RequestParam Integer pageIndex) {
        return userInfoService.adminGetUserInfoList(playerType, pageSize, pageIndex);
    }

    @PostMapping("/adminGetUserInfo")
    public ApiResult adminGetUserInfo(@RequestBody String idDetail) throws IOException {
        return userInfoService.adminGetUserInfo(idDetail);
    }

    @PostMapping("/adminUploadIdPhotoBase64")
    public ApiResult adminUploadIdPhotoBase64(@RequestParam String photoName, @RequestParam String userIdDetail, @RequestBody String base64, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.adminUploadIdPhotoBase64(photoName, userIdDetail, base64, request, response);
    }

    @PostMapping("/adminUploadIdPhoto")
    public ApiResult adminUploadIdPhoto(@RequestParam String photoName, @RequestParam String userIdDetail, @RequestBody MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return userInfoService.adminUploadIdPhoto(photoName, userIdDetail, file, request, response);
    }

    @PostMapping("/adminSetUserInfo")
    public ApiResult adminSetUserInfo(@RequestBody String userInfoStr, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.adminSetUserInfo(userInfoStr, request, response);
    }

    @GetMapping("/adminGetIdCheckList")
    public ApiResult adminGetIdCheckList(@RequestParam String playerType, @RequestParam Integer pageSize, @RequestParam Integer pageIndex) {
        return userInfoService.adminGetIdCheckList(playerType, pageSize, pageIndex);
    }

    @PostMapping("/adminGetIdCheck")
    public ApiResult adminGetIdCheck(@RequestParam String idCheckApplyTime, @RequestBody String idDetail) throws IOException {
        return userInfoService.adminGetIdCheck(idCheckApplyTime, idDetail);
    }

    @PostMapping("/adminSetIdCheck")
    public ApiResult adminSetIdCheck(@RequestParam String idCheckApplyTime, @RequestBody String idCheckStr, HttpServletRequest request) {
        return userInfoService.adminSetIdCheck(idCheckApplyTime, idCheckStr, request);
    }


}
