package com.myworld.cauth.secure.service;


import com.myworld.cauth.secure.data.entity.IdCheck;
import com.myworld.cauth.secure.data.entity.UserInfo;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.SimpleUser;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

public interface UserInfoService {

    SimpleUser makeSimpleUser(UserInfo userInfo, String token);

    ApiResult getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException;
    ApiResult setUserInfo(String userInfoStr, HttpServletRequest request, HttpServletResponse response);
    ApiResult setIdInfo(String idnumber, HttpServletRequest request, HttpServletResponse response);
    void makeIdCheck(UserInfo user, String idnumber, Timestamp timestamp);
    ApiResult getUserInfoBasic(HttpServletRequest request, HttpServletResponse response);
    ApiResult saveNickname(String nickname, HttpServletRequest request, HttpServletResponse response);
    ApiResult getIdCheckResultReason(HttpServletRequest request, HttpServletResponse response);

    ApiResult uploadUserAvatarBase64(String base64, HttpServletRequest request, HttpServletResponse response);
    void getAvatar(String id, HttpServletResponse response) throws IOException;

    ApiResult uploadIdPhotoBase64(String photoName, String base64, HttpServletRequest request, HttpServletResponse response);
    ApiResult uploadIdPhoto(String photoName, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException;
    String saveIdPhoto(String creator, String userIdDetail, String photoName, byte[] fileBytes, String extensionType, String fileName);
    void updateUserInfoPhoto(String photoName, String idDetail, String creator, String token);
    void getFileLocation(String idDetail, HttpServletResponse response) throws IOException;
    ApiResult getBase64File(String idDetail);

    // 以下为admin部分
    ApiResult adminGetUserInfoList(String playerType, Integer pageSize, Integer pageIndex);
    ApiResult adminGetUserInfo(String idDetail) throws IOException;

    ApiResult adminUploadIdPhotoBase64(String photoName, String userIdDetail, String base64, HttpServletRequest request, HttpServletResponse response);
    ApiResult adminUploadIdPhoto(String photoName, String userIdDetail, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException;

    ApiResult adminSetUserInfo(String userInfoStr, HttpServletRequest request, HttpServletResponse response);
    ApiResult adminGetIdCheckList(String playerType, Integer pageSize, Integer pageIndex);
    ApiResult adminGetIdCheck(String idCheckApplyTime, String idDetail) throws IOException;
    ApiResult adminSetIdCheck(String idCheckApplyTime, String idCheckStr, HttpServletRequest request);
    void updateUserInfoDoneIdCheck(IdCheck idCheck, String token);

    // 以下为feign部分
    void initPhoneUser(UserInfo userInfo, String token);
    void syncPhoneUser(UserInfo userInfo, String token);

}
