package com.myworld.cauth.secure.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.cauth.secure.data.entity.*;
import com.myworld.cauth.secure.data.model.UserInfoSysuser;
import com.myworld.cauth.secure.data.repository.IdCheckRepository;
import com.myworld.cauth.secure.data.repository.IdPhotoRepository;
import com.myworld.cauth.secure.data.repository.UserAvatarRepository;
import com.myworld.cauth.secure.data.repository.UserInfoRepository;
import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.redis.JwtCheckService;
import com.myworld.cauth.common.UtilService;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.SimpleUser;
import com.myworld.cauth.common.util.JwtUtil;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private static Logger log = LogManager.getRootLogger();

    private UserInfoRepository userInfoRepository;
    private IdCheckRepository idCheckRepository;
    private IdPhotoRepository idPhotoRepository;
    private UtilService utilService;
    private JwtCheckService jwtCheckService;
    private ChatFeignService chatFeignService;
    private UserAvatarRepository userAvatarRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserInfoServiceImpl(
        UserInfoRepository userInfoRepository,
        IdCheckRepository idCheckRepository,
        IdPhotoRepository idPhotoRepository,
        UtilService utilService,
        JwtCheckService jwtCheckService,
        ChatFeignService chatFeignService,
        UserAvatarRepository userAvatarRepository
    ) {
        this.userInfoRepository = userInfoRepository;
        this.idCheckRepository = idCheckRepository;
        this.idPhotoRepository = idPhotoRepository;
        this.utilService = utilService;
        this.jwtCheckService = jwtCheckService;
        this.chatFeignService = chatFeignService;
        this.userAvatarRepository = userAvatarRepository;
    }

    @Override
    public SimpleUser makeSimpleUser(UserInfo userInfo, String token) {
        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setWid(userInfo.getIdDetail());
        simpleUser.setToken(token);
        simpleUser.setUsername(userInfo.getUsername());
        simpleUser.setNickname(userInfo.getNickname());
        simpleUser.setPlayerType(userInfo.getPlayerType());
        simpleUser.setOffer(userInfo.getOffer());
        return simpleUser;
    }

    @Override
    public ApiResult getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String idDetail = jwtCheckService.checkJwtToken(request, response);
        if (idDetail == null) {
            return ResultUtil.error(-2, "请先登录");
        }
        UserInfo userInfo = userInfoRepository.findByIdDetail(idDetail);
        if (userInfo == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }

        UserInfoSysuser user = new UserInfoSysuser();
        user.setAccountStatus(userInfo.getAccountStatus());
        user.setPlayerType(userInfo.getPlayerType());
        user.setRealname(userInfo.getRealname());
        user.setEmail(userInfo.getEmail());
        user.setIdtype(userInfo.getIdtype());
        user.setOffer(userInfo.getOffer());
        user.setTheposition(userInfo.getTheposition());
        user.setTheindustry(userInfo.getTheindustry());
        user.setCompanyname(userInfo.getCompanyname());
        user.setPlacebelong(userInfo.getPlacebelong());
        user.setAddress(userInfo.getAddress());

        String userInfoStr = objectMapper.writeValueAsString(user);
        String userInfoStrEncode = MyUserKeyService.aesEncrypt(userInfoStr);
        return ResultUtil.success(userInfoStrEncode);
    }

    @Override
    public ApiResult setUserInfo(String userInfoStr, HttpServletRequest request, HttpServletResponse response){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String idDetail = jwtCheckService.checkJwtToken(request, response);
        if (idDetail == null) {
            return ResultUtil.error(-2, "请先登录");
        }

        userInfoStr = userInfoStr.replace(" ", "+");
        String userInfoStrDecoded = MyUserKeyService.aesDecrypt(userInfoStr);
        JSONObject jsonObject =JSONObject.parseObject(userInfoStrDecoded);
        UserInfo userInfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);

        UserInfo user = userInfoRepository.findByIdDetail(idDetail);
        if (user == null) {
            return ResultUtil.error(-2, "未找到相应的用户");
        }

        String accountStatus = user.getAccountStatus();
        if (accountStatus.equals("认证中")) {
            return ResultUtil.error(-2, "实名认证正在审核中，不能更新用户信息");
        }
        user.setRealname(userInfo.getRealname());
        user.setEmail(userInfo.getEmail());
        user.setIdtype(userInfo.getIdtype());
        user.setOffer(userInfo.getOffer());
        user.setPlayerType(userInfo.getPlayerType());
        user.setTheposition(userInfo.getTheposition());
        user.setTheindustry(userInfo.getTheindustry());
        user.setCompanyname(userInfo.getCompanyname());
        user.setPlacebelong(userInfo.getPlacebelong());
        user.setAddress(userInfo.getAddress());
        user.setUpdater(user.getUsername());
        user.setUpdateTime(timestamp);
        user.setAccountStatus("待认证");
        userInfoRepository.save(user);
        log.info("用户更新用户信息完成！！");

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.syncPhoneUser(userInfo, token);

        return ResultUtil.success();
    }

    @Override
    public ApiResult setIdInfo(String idnumber, HttpServletRequest request, HttpServletResponse response){
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String idDetail = jwtCheckService.checkJwtToken(request, response);
        if (idDetail == null) {
            return ResultUtil.error(-2, "请先登录");
        }
        idnumber = idnumber.replace(" ", "+");
        String realIdnumber = MyUserKeyService.aesDecrypt(idnumber);

        UserInfo user = userInfoRepository.findByIdDetail(idDetail);
        if (user == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }
        user.setIdnumber(realIdnumber);
        user.setIdCheckApplyTime(timestamp);
        user.setAccountStatus("认证中");
        userInfoRepository.save(user);
        this.makeIdCheck(user, realIdnumber, timestamp);
        return ResultUtil.success();
    }

    @Override
    public void makeIdCheck(UserInfo user, String idnumber, Timestamp timestamp){
        //用于在数据库中生成实名认证申请记录userIdCheck
        IdCheck ic = new IdCheck();
        ic.setIdDetail(user.getIdDetail());
        ic.setIdCheckApplyTime(timestamp);
        ic.setPlayerType(user.getPlayerType());
        ic.setOffer(user.getOffer());
        ic.setUsername(user.getUsername());
        ic.setNickname(user.getNickname());
        ic.setRealname(user.getRealname());
        ic.setEmail(user.getEmail());
        ic.setIdtype(user.getIdtype());
        ic.setTheposition(user.getTheposition());
        ic.setTheindustry(user.getTheindustry());
        ic.setPlacebelong(user.getPlacebelong());
        ic.setCompanyname(user.getCompanyname());
        ic.setAddress(user.getAddress());
        ic.setIdnumber(idnumber);

        ic.setIdPhotoFront(user.getIdPhotoFront());
        ic.setIdPhotoBack(user.getIdPhotoBack());
        ic.setBusinesscard(user.getBusinesscard());
        ic.setWorkerscard(user.getWorkerscard());
        ic.setCreditpaper(user.getCreditpaper());

        ic.setAlreadySetup("NO");
        idCheckRepository.save(ic);
    }

    @Override
    public ApiResult getUserInfoBasic(HttpServletRequest request, HttpServletResponse response) {
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();

        UserInfo user = userInfoRepository.findByIdDetail(idDetail);
        Optional<UserAvatar> optional = userAvatarRepository.findById(idDetail);
        String base64 = null;
        if (optional.isPresent()) {
            base64 = "data:image/jpeg;base64," + utilService.bytesToBase64(optional.get().getFileByte());
        }
        Map<String, String> map = new HashMap<>();
        map.put("nickname", user.getNickname());
        map.put("accountStatus", user.getAccountStatus());
        map.put("userAvatar", base64);
        return ResultUtil.success(map);
    }

    @Override
    public ApiResult getIdCheckResultReason(HttpServletRequest request, HttpServletResponse response) {
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();

        UserInfo user = userInfoRepository.findByIdDetail(idDetail);
        if (user == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }
        Map<String, String> map = new HashMap<>();
        map.put("idCheckResult", user.getIdCheckResult());
        map.put("idCheckReasons", user.getIdCheckReasons());
        return ResultUtil.success(map);
    }


    @Override
    public ApiResult saveNickname(String nickname, HttpServletRequest request, HttpServletResponse response) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userIdDetail = userDetail.getIdDetail();

        UserInfo userInfo = userInfoRepository.findByIdDetail(userIdDetail);
        if (userInfo == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }
        String username = userInfo.getUsername();
        userInfo.setNickname(nickname);
        userInfo.setUpdater(username);
        userInfo.setUpdateTime(timestamp);
        userInfoRepository.save(userInfo);

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.syncPhoneUser(userInfo, token);

        return ResultUtil.success();
    }

    @Override
    public ApiResult uploadUserAvatarBase64(String base64, HttpServletRequest request, HttpServletResponse response) {
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userIdDetail = userDetail.getIdDetail();

        UserAvatar userAvatar = new UserAvatar();
        Optional<UserAvatar> optional = userAvatarRepository.findById(userIdDetail);
        if (optional.isPresent()) {
            userAvatar = optional.get();
        } else {
            userAvatar.setId(userIdDetail);
        }

        byte[] base64Bytes = this.utilService.base64ToBytes(base64);
        if (base64Bytes == null) {
            return ResultUtil.error(-2, "base64字符串解析失败");
        }

        userAvatar.setFileName(userIdDetail + ".jpg");
        userAvatar.setExtensionType("image/jpeg");
        userAvatar.setFileByte(base64Bytes);
        userAvatarRepository.save(userAvatar);
        return ResultUtil.success();
    }

    @Override
    public void getAvatar(String id, HttpServletResponse response) throws IOException {
        Optional<UserAvatar> optional = userAvatarRepository.findById(id);
        if (optional.isPresent()) {
            UserAvatar userAvatar = optional.get();
            IOUtils.copy(new ByteArrayInputStream(userAvatar.getFileByte()), response.getOutputStream());
            response.setContentType(userAvatar.getExtensionType());
        }
    }

    @Override
    public ApiResult uploadIdPhotoBase64(String photoName, String base64, HttpServletRequest request, HttpServletResponse response){
        log.info("【开始执行uploadIdPhotoBase64方法，上传的base64是：{}】", base64);
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userIdDetail = userDetail.getIdDetail();

        UserInfo userInfo = userInfoRepository.findByIdDetail(userIdDetail);
        if (userInfo == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }

        byte[] base64Bytes = this.utilService.base64ToBytes(base64);
        if (base64Bytes == null) {
            return ResultUtil.error(-2, "base64字符串解析失败");
        }

        String fileName = photoName + ".jpg";
        String idDetail = this.saveIdPhoto(userIdDetail, userIdDetail, photoName, base64Bytes, "image/jpeg", fileName);

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.updateUserInfoPhoto(photoName, idDetail, userIdDetail, token);
        return ResultUtil.success();
    }


    @Override
    public ApiResult uploadIdPhoto(String photoName, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("【开始执行uploadIdPhoto方法】");
        SysUser userDetail = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userIdDetail = userDetail.getIdDetail();

        UserInfo userInfo = userInfoRepository.findByIdDetail(userIdDetail);
        if (userInfo == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }

        // SysUser userDetails = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // String creator = userDetails.getUsername();
        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        String idDetail = this.saveIdPhoto(userIdDetail, userIdDetail, photoName, file.getBytes(), file.getContentType(), file.getOriginalFilename());
        this.updateUserInfoPhoto(photoName, idDetail, userIdDetail, token);

        String base64 = "data:image/jpeg;base64," + utilService.bytesToBase64(file.getBytes());
        return ResultUtil.success(base64);
    }

    @Override
    public String saveIdPhoto(String creator, String userIdDetail, String photoName, byte[] fileBytes, String extensionType, String fileName) {
        long theTime = new Date().getTime();
        Timestamp timestamp = new Timestamp(theTime);
        String idDetail = userIdDetail + "-" + theTime;

        //根据用户名和照片名称，查找是否已经存在该照片
        IdPhoto idPhoto = idPhotoRepository.findByUserIdDetailAndPhotoName(userIdDetail, photoName);
        if (idPhoto == null){
            idPhoto = new IdPhoto();
            idPhoto.setUserIdDetail(userIdDetail);
            idPhoto.setPhotoName(photoName);
        }
        //更新idDetail
        idPhoto.setIdDetail(idDetail);
        idPhoto.setCreator(creator);
        idPhoto.setFileByte(fileBytes);
        idPhoto.setExtensionType(extensionType);
        idPhoto.setFileName(fileName);
        //设置更新时间，并设置idDetail，用于页面显示，更新后的图片
        idPhoto.setUpdateTime(timestamp);
        idPhotoRepository.save(idPhoto);
        return idDetail;
    }


    @Override
    public void updateUserInfoPhoto(String photoName, String idDetail, String userIdDetail, String token) {
        UserInfo userInfo = userInfoRepository.findByIdDetail(userIdDetail);
        String fileLocation = "/getFileLocation/" + idDetail;
        //根据照片名称更新UserInfo中的照片地址
        switch (photoName) {
            case "idPhotoFront":
                userInfo.setIdPhotoFront(fileLocation);
                break;
            case "idPhotoBack":
                userInfo.setIdPhotoBack(fileLocation);
                break;
            case "businesscard":
                userInfo.setBusinesscard(fileLocation);
                break;
            case "workerscard":
                userInfo.setWorkerscard(fileLocation);
                break;
            case "creditpaper":
                userInfo.setCreditpaper(fileLocation);
                break;
        }
        userInfoRepository.save(userInfo);
    }

    @Override
    public void getFileLocation(String idDetail, HttpServletResponse response) throws IOException {
        IdPhoto idPhoto = idPhotoRepository.findByIdDetail(idDetail);
        if (idPhoto != null) {
            IOUtils.copy(new ByteArrayInputStream(idPhoto.getFileByte()), response.getOutputStream());
            response.setContentType(idPhoto.getExtensionType());
        }
    }

    @Override
    public ApiResult getBase64File(String idDetail) {
        IdPhoto idPhoto = idPhotoRepository.findByIdDetail(idDetail);
        String base64 = "data:image/jpeg;base64," + utilService.bytesToBase64(idPhoto.getFileByte());
        return ResultUtil.success(base64);
    }


    @Override
    public ApiResult adminGetUserInfoList(String playerType, Integer pageSize, Integer pageIndex){
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime");
        Page<UserInfo> userInfos = userInfoRepository.findByPlayerType(playerType, pageable);
        Long theNum = userInfos.getTotalElements();
        List<UserInfo> userInfoList = userInfos.getContent();
        List<Map> list = new ArrayList<>();
        for (UserInfo ui : userInfoList) {
            Map<String, Object> map = new HashMap<>();
            String userIdDetailEncode = MyUserKeyService.encodeUserIdDetail(ui.getIdDetail());
            map.put("idDetail", userIdDetailEncode);
            map.put("username", ui.getUsername());
            map.put("accountStatus", ui.getAccountStatus());
            map.put("realname", ui.getRealname());
            map.put("placebelong", ui.getPlacebelong());
            map.put("idCheckReleaseTime", ui.getIdCheckReleaseTime());
            list.add(map);
        }
        return ResultUtil.success(theNum, list);
    }


    @Override
    public ApiResult adminGetUserInfo(String idDetail) throws IOException{
        String userIdDetail = MyUserKeyService.decodeUserIdDetail(idDetail);
        UserInfo userInfo = userInfoRepository.findByIdDetail(userIdDetail);
        String userInfoStr = objectMapper.writeValueAsString(userInfo);
        String userInfoStrEncode = MyUserKeyService.aesEncrypt(userInfoStr);
        return ResultUtil.success(userInfoStrEncode);
    }

    @Override
    public ApiResult adminUploadIdPhotoBase64(String photoName, String userIdDetail, String base64, HttpServletRequest request, HttpServletResponse response) {
        log.info("【开始执行adminUploadIdPhotoBase64方法，上传的base64是：{}】", base64);
        String adminIdDetail = jwtCheckService.checkJwtToken(request, response);
        if (userIdDetail == null) {
            return ResultUtil.error(-2, "请先登录");
        }
        String realUserIdDetail = MyUserKeyService.decodeUserIdDetail(userIdDetail);
        UserInfo userInfo = userInfoRepository.findByIdDetail(realUserIdDetail);
        if (userInfo == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }

        byte[] base64Bytes = this.utilService.base64ToBytes(base64);
        if (base64Bytes == null) {
            return ResultUtil.error(-2, "base64字符串解析失败");
        }

        String fileName = photoName + ".jpg";
        String idDetail = this.saveIdPhoto(adminIdDetail, realUserIdDetail, photoName, base64Bytes, "image/jpeg", fileName);
        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.updateUserInfoPhoto(photoName, idDetail, realUserIdDetail, token);

        String fileLocation = "/getFileLocation/" + idDetail;
        return ResultUtil.success(fileLocation);
    }

    @Override
    public ApiResult adminUploadIdPhoto(String photoName, String userIdDetail, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("【开始执行adminUploadIdPhoto方法】");
        String adminIdDetail = jwtCheckService.checkJwtToken(request, response);
        if (adminIdDetail == null) {
            return ResultUtil.error(-2, "请先登录");
        }
        String realUserIdDetail = MyUserKeyService.decodeUserIdDetail(userIdDetail);
        UserInfo userInfo = userInfoRepository.findByIdDetail(realUserIdDetail);
        if (userInfo == null) {
            return ResultUtil.error(-2, "未能找到该用户");
        }

        String idDetail = this.saveIdPhoto(adminIdDetail, realUserIdDetail, photoName, file.getBytes(), file.getContentType(), file.getOriginalFilename());
        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.updateUserInfoPhoto(photoName, idDetail, realUserIdDetail, token);

        String fileLocation = "/getFileLocation/" + idDetail;
        return ResultUtil.success(fileLocation);
    }

    @Override
    public ApiResult adminSetUserInfo(String userInfoStr, HttpServletRequest request, HttpServletResponse response) {
        log.info("传上来的userInfoStr是：{}", userInfoStr);
        String adminIdDetail = jwtCheckService.checkJwtToken(request, response);
        if (adminIdDetail == null) {
            return ResultUtil.error(-2, "请先登录");
        }
        userInfoStr = userInfoStr.replace(" ", "+");
        String userInfoStrDecoded = MyUserKeyService.aesDecrypt(userInfoStr);
        JSONObject jsonObject =JSONObject.parseObject(userInfoStrDecoded);
        UserInfo userInfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);
        String userIdDetail = userInfo.getIdDetail();
        log.info("解析出来的userIdDetail是：{}", userIdDetail);
        UserInfo user = userInfoRepository.findByIdDetail(userIdDetail);
        if (user == null) {
            return ResultUtil.error(-2, "未找到相应的用户");
        }
        Timestamp timestamp = new Timestamp(new Date().getTime());
        user.setUsername(userInfo.getUsername());
        user.setNickname(userInfo.getNickname());
        user.setPlayerType(userInfo.getPlayerType());
        user.setAccountStatus(userInfo.getAccountStatus());
        user.setOffer(userInfo.getOffer());

        user.setRealname(userInfo.getRealname());
        user.setEmail(userInfo.getEmail());
        user.setIdtype(userInfo.getIdtype());
        user.setTheposition(userInfo.getTheposition());
        user.setTheindustry(userInfo.getTheindustry());
        user.setCompanyname(userInfo.getCompanyname());
        user.setPlacebelong(userInfo.getPlacebelong());
        user.setAddress(userInfo.getAddress());
        user.setIdnumber(userInfo.getIdnumber());
        user.setCreditLevel(userInfo.getCreditLevel());
        user.setCreditLevelConfirm(userInfo.getCreditLevelConfirm());
        user.setUpdateTime(timestamp);
        user.setUpdater(adminIdDetail);

        userInfoRepository.save(user);

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.syncPhoneUser(userInfo, token);
        return ResultUtil.success();
    }


    @Override
    public ApiResult adminGetIdCheckList(String playerType, Integer pageSize, Integer pageIndex) {
        log.info("【开始执行adminGetIdCheckList方法】");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "idCheckApplyTime");
        Page<IdCheck> idChecks = idCheckRepository.findByPlayerType(playerType, pageable);
        Long theNum = idChecks.getTotalElements();
        List<IdCheck> idCheckList = idChecks.getContent();
        List<Map> list = new ArrayList<>();
        for (IdCheck ic : idCheckList) {
            Map<String, Object> map = new HashMap<>();
            String userIdDetailEncode = MyUserKeyService.encodeUserIdDetail(ic.getIdDetail());
            map.put("idDetail", userIdDetailEncode);
            map.put("username", ic.getUsername());
            map.put("realname", ic.getRealname());
            map.put("idCheckResult", ic.getIdCheckResult());
            map.put("idCheckApplyTime", ic.getIdCheckApplyTime());
            map.put("idCheckCheckTime", ic.getIdCheckCheckTime());
            map.put("idCheckReleaseTime", ic.getIdCheckReleaseTime());
            list.add(map);
        }
        return ResultUtil.success(theNum, list);
    }

    @Override
    public ApiResult adminGetIdCheck(String idCheckApplyTime, String idDetail) throws IOException {
        Timestamp theApplyTime = new Timestamp(Long.parseLong(idCheckApplyTime));
        String userIdDetail = MyUserKeyService.decodeUserIdDetail(idDetail);
        IdCheck idCheck = idCheckRepository.findByIdDetailAndIdCheckApplyTime(userIdDetail, theApplyTime);
        if (idCheck == null) {
            return ResultUtil.error(-2, "未能找到相应的实名认证记录");
        }
        Timestamp releaseTime = idCheck.getIdCheckReleaseTime();
        if (releaseTime == null){
            Timestamp timestamp = new Timestamp(new Date().getTime());
            idCheck.setIdCheckCheckTime(timestamp);
            idCheckRepository.save(idCheck);
        }
        String idCheckStr = objectMapper.writeValueAsString(idCheck);
        String idCheckStrEncode = MyUserKeyService.aesEncrypt(idCheckStr);
        return ResultUtil.success(idCheckStrEncode);
    }

    @Override
    public ApiResult adminSetIdCheck(String idCheckApplyTime, String idCheckStr, HttpServletRequest request) {
        log.info("【开始执行adminSetIdCheck方法】");
        Timestamp theApplyTime = new Timestamp(Long.parseLong(idCheckApplyTime));
        idCheckStr = idCheckStr.replace(" ", "+");
        String idCheckStrDecoded = MyUserKeyService.aesDecrypt(idCheckStr);
        JSONObject jsonObject =JSONObject.parseObject(idCheckStrDecoded);
        IdCheck idCheck = JSONObject.toJavaObject(jsonObject, IdCheck.class);
        String userIdDetail = idCheck.getIdDetail();
        IdCheck ic = idCheckRepository.findByIdDetailAndIdCheckApplyTime(userIdDetail, theApplyTime);
        if (ic == null) {
            return ResultUtil.error(-2, "未能找到相应的实名认证记录");
        }
        Timestamp timestamp = new Timestamp(new Date().getTime());
        ic.setOffer(idCheck.getOffer());
        ic.setIdCheckResult(idCheck.getIdCheckResult());
        ic.setIdCheckReasons(idCheck.getIdCheckReasons());
        ic.setIdCheckMemo(idCheck.getIdCheckMemo());
        ic.setIdCheckReleaseTime(timestamp);
        ic.setAlreadySetup("YES");
        idCheckRepository.save(ic);

        String token = request.getHeader(JwtUtil.HEADER_AUTH);
        this.updateUserInfoDoneIdCheck(ic, token);
        return ResultUtil.success();
    }

    @Override
    public void updateUserInfoDoneIdCheck(IdCheck idCheck, String token) {
        String idDetail = idCheck.getIdDetail();
        String result = idCheck.getIdCheckResult();
        UserInfo userInfo = userInfoRepository.findByIdDetail(idDetail);
        userInfo.setOffer(idCheck.getOffer());
        userInfo.setIdCheckCheckTime(idCheck.getIdCheckCheckTime());
        userInfo.setIdCheckResult(result);
        userInfo.setIdCheckReasons(idCheck.getIdCheckReasons());
        userInfo.setIdCheckMemo(idCheck.getIdCheckMemo());
        userInfo.setIdCheckReleaseTime(idCheck.getIdCheckReleaseTime());

        if (result.equals("PASS")){
            userInfo.setAccountStatus("已认证");
        }else if (result.equals("DENY")) {
            userInfo.setAccountStatus("待认证");
        }
        userInfoRepository.save(userInfo);
        this.syncPhoneUser(userInfo, token);
    }

    @Override
    public void initPhoneUser(UserInfo userInfo, String token) {
        log.info("【开始执行initPhoneUser方法】");
        SimpleUser simpleUser = this.makeSimpleUser(userInfo, token);
        ApiResult apiResult = this.chatFeignService.initPhoneUser(simpleUser);
        if (!apiResult.getCode().equals(0)){
            log.info("【initPhoneUser方法，同步数据不成功，需将数据保存至数据库，通过定时任务再重新保存，{}, {}】", apiResult.getCode(), apiResult.getMsg());
        }
    }


    @Override
    public void syncPhoneUser(UserInfo userInfo, String token) {
        log.info("【开始执行syncPhoneUser方法】");
        SimpleUser simpleUser = this.makeSimpleUser(userInfo, token);
        ApiResult apiResult = this.chatFeignService.syncPhoneUser(simpleUser);
        log.info("【syncPhoneUser方法，已获取到feign返回的远程result信息】");
        if (!apiResult.getCode().equals(0)){
            log.info("【syncPhoneUser方法，同步数据不成功，需将数据保存至数据库，通过定时任务再重新保存，{}, {}】", apiResult.getCode(), apiResult.getMsg());
        }
    }

}
