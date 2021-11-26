package com.myworld.cauth.siteinfo.service;

import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.security.redis.JwtCheckService;
import com.myworld.cauth.siteinfo.entity.*;
import com.myworld.cauth.siteinfo.modal.MyFileList;
import com.myworld.cauth.siteinfo.repository.*;
import com.myworld.cauth.common.model.FileUploadResult;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.properties.SecurityConstants;
import com.myworld.cauth.common.UtilService;
import com.myworld.cauth.common.util.ResultUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class FileServiceImpl implements FileService {
    private static Logger log = LogManager.getRootLogger();
    private SmallPicRepository smallPicRepository;
    private TinymcePhotoRepository tinymcePhotoRepository;
    private MyFileRepository myFileRepository;
    private UtilService utilService;
    private WritingRepository writingRepository;
    private MyLinkRepository myLinkRepository;

    public FileServiceImpl(
            SmallPicRepository smallPicRepository,
            TinymcePhotoRepository tinymcePhotoRepository,
            MyFileRepository myFileRepository,
            UtilService utilService,
            WritingRepository writingRepository,
            MyLinkRepository myLinkRepository
    ) {
        this.smallPicRepository = smallPicRepository;
        this.tinymcePhotoRepository = tinymcePhotoRepository;
        this.myFileRepository = myFileRepository;
        this.utilService = utilService;
        this.writingRepository = writingRepository;
        this.myLinkRepository = myLinkRepository;
    }

    /**
     * 制作水印图片
     */
    @Override
    public ByteArrayOutputStream makePhotoWithWatermark(ByteArrayInputStream byteArrayInputStream) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 获取 watermark水印 图片
        MyFile myFile = myFileRepository.findByOfficialName("WATERMARK");
        if (myFile != null) {
            BufferedImage watermark = ImageIO.read(new ByteArrayInputStream(myFile.getFileByte()));
            Thumbnails.of(byteArrayInputStream).scale(1f).watermark(Positions.BOTTOM_RIGHT, watermark, 1f).toOutputStream(baos);
        } else {
            Thumbnails.of(byteArrayInputStream).scale(1f).toOutputStream(baos);
        }
        return baos;
    }


    @Override
    public ApiResult adminUploadSmallPic(String idDetail, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String extensionType = file.getContentType();
        String fileName = file.getOriginalFilename();
        return this.adminSaveSmallPic(idDetail, bytes, fileName, extensionType);
    }

    @Override
    public ApiResult adminUploadSmallPicBase64(String idDetail, String base64) {
        byte[] base64Bytes = this.utilService.base64ToBytes(base64);
        if (base64Bytes == null) {
            return ResultUtil.error(-2, "base64字符串解析失败");
        }
        String fileName = idDetail + "-base64.jpg";
        String extensionType = "image/jpeg";
        return this.adminSaveSmallPic(idDetail, base64Bytes, fileName, extensionType);
    }

    @Override
    public ApiResult adminSaveSmallPic(String idDetail, byte[] fileBytes, String fileName, String extensionType) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        // 使用文章的idDetail来一一对应缩略图的idDetail, 一篇文章只能有一张缩略图
        SmallPic smallPic = smallPicRepository.findByIdDetail(idDetail);
        if (smallPic == null){
            smallPic = new SmallPic();
            smallPic.setIdDetail(idDetail);
        }

        Writing writing = writingRepository.findByIdDetail(idDetail);
        String creator = writing.getCreator();

        smallPic.setFileByte(fileBytes);
        smallPic.setExtensionType(extensionType);
        smallPic.setFileName(fileName);
        smallPic.setCreator(creator);
        smallPic.setUpdateTime(timestamp);
        smallPicRepository.save(smallPic);

        String fileLocation = "/getSmallPicLocation/" + idDetail;
        return ResultUtil.success(fileLocation);
    }




    @Override
    public FileUploadResult adminUploadTinymce(String fileUsage, String idDetail, MultipartFile file) throws IOException {
        Writing writing = writingRepository.findByIdDetail(idDetail);
        if (writing == null) {
            return new FileUploadResult();
        }
        String creator = writing.getCreator();

        Timestamp ts = new Timestamp(new Date().getTime());
        //tinymce中的图片编号规则，creator统一使用ADMIN，防止管理员用户名泄露
        String tinyIdDetail = "TINY-" + new Date().getTime();

        //增加水印
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes());
        ByteArrayOutputStream baos = this.makePhotoWithWatermark(byteArrayInputStream);
        byte[] theBytes = baos.toByteArray();

        TinymcePhoto tinymcePhoto = new TinymcePhoto();
        tinymcePhoto.setFileByte(theBytes);
        tinymcePhoto.setExtensionType(file.getContentType());
        tinymcePhoto.setFileName(file.getOriginalFilename());

        tinymcePhoto.setCreator(creator);
        tinymcePhoto.setIdDetail(tinyIdDetail);
        tinymcePhoto.setUsageIdDetail(idDetail);
        tinymcePhoto.setFileUsage(fileUsage);
        tinymcePhoto.setOnUse("NO");
        tinymcePhoto.setUpdateTime(ts);
        tinymcePhotoRepository.save(tinymcePhoto);

        String fileLocation = "/getTinymcePhotoLocation/"+ tinyIdDetail;
        FileUploadResult result = new FileUploadResult();
        result.setFilename(tinymcePhoto.getFileName());
        result.setLocation(fileLocation);
        return result;
    }


    @Override
    public ApiResult adminGetFileList() {
        List<MyFile> myFiles = myFileRepository.findAll(Sort.by(Sort.Direction.DESC, "updateTime"));
        List<MyFileList> myFileLists = new ArrayList<>();
        for (MyFile mf : myFiles){
            MyFileList myFileList = new MyFileList();
            myFileList.setCreateTime(mf.getCreateTime());
            myFileList.setUpdateTime(mf.getUpdateTime());
            myFileList.setOfficialName(mf.getOfficialName());
            myFileList.setVersionNumber(mf.getVersionNumber());
            myFileList.setFileName(mf.getFileName());
            myFileList.setCreator(mf.getCreator());
            myFileLists.add(myFileList);
        }
        return ResultUtil.success(myFileLists);
    }

    @Override
    public ApiResult adminUploadFile(MultipartFile file, String officialName, String versionNumber) throws IOException{
        Timestamp timestamp = new Timestamp(new Date().getTime());
        SysAdmin userDetail = (SysAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idDetail = userDetail.getIdDetail();

        MyFile myFile = myFileRepository.findByOfficialName(officialName);
        if (myFile == null){
            myFile = new MyFile();
            String fileIdDetail = "F" + new Date().getTime();
            myFile.setIdDetail(fileIdDetail);
            myFile.setOfficialName(officialName);
        }
        myFile.setFileByte(file.getBytes());
        myFile.setExtensionType(file.getContentType());
        myFile.setFileName(file.getOriginalFilename());

        myFile.setVersionNumber(versionNumber);
        myFile.setCreator(idDetail);
        myFile.setUpdateTime(timestamp);
        myFileRepository.save(myFile);
        return ResultUtil.success();
    }

    @Override
    public ApiResult adminDeleteFile(String officialName){
        myFileRepository.deleteByOfficialName(officialName);
        return ResultUtil.success();
    }


    @Override
    public ApiResult adminGetLinkList() {
        List<MyLink> myLinks = myLinkRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        return ResultUtil.success(myLinks);
    }

    @Override
    public ApiResult adminSetLink(String officialName, String memo, String linkContent){
        SysAdmin userDetail = (SysAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String adminIdDetail = userDetail.getIdDetail();
        Long theTime = new Date().getTime();
        Timestamp timestamp = new Timestamp(theTime);

        MyLink myLink = myLinkRepository.findByOfficialName(officialName);
        if (myLink == null) {
            myLink = new MyLink();
            String idDetail = "LINK" + theTime;
            myLink.setIdDetail(idDetail);
        }
        myLink.setOfficialName(officialName);
        myLink.setMemo(memo);
        myLink.setLinkContent(linkContent);

        myLink.setCreateTime(timestamp);
        myLink.setCreator(adminIdDetail);
        myLinkRepository.save(myLink);
        return ResultUtil.success();
    }

    @Override
    public ApiResult adminDelLink(String officialName){
        myLinkRepository.deleteByOfficialName(officialName);
        return ResultUtil.success();
    }



    @Override
    public void getTinymcePhotoLocation(String idDetail, HttpServletResponse response) throws IOException {
        TinymcePhoto tinymcePhoto = tinymcePhotoRepository.findByIdDetail(idDetail);
        IOUtils.copy(new ByteArrayInputStream(tinymcePhoto.getFileByte()) , response.getOutputStream());
        response.setContentType(tinymcePhoto.getExtensionType());
    }

    @Override
    public void getSmallPicLocation(String idDetail, HttpServletResponse response) throws IOException {
        SmallPic smallPic = smallPicRepository.findByIdDetail(idDetail);
        IOUtils.copy(new ByteArrayInputStream(smallPic.getFileByte()), response.getOutputStream());
        response.setContentType(smallPic.getExtensionType());
    }

    @Override
    public void getMyFile(String officialName, HttpServletResponse response) throws IOException{
        MyFile myFile = myFileRepository.findByOfficialName(officialName);
        IOUtils.copy(new ByteArrayInputStream(myFile.getFileByte()), response.getOutputStream());
        response.setContentType(myFile.getExtensionType());
    }




    @Override
    public ApiResult getMyLinkAPK(){
        MyLink myLink = myLinkRepository.findByOfficialName("APKBAIDUPAN");
        if (myLink == null){
            return ResultUtil.error(-2, "没有此链接");
        }
        return ResultUtil.success(myLink.getLinkContent());
    }

    @Override
    public ApiResult getAppInfoOnline() {
        MyFile myFile = myFileRepository.findByOfficialName(SecurityConstants.APP_OFFICIAL_NAME);
        return ResultUtil.success(myFile.getVersionNumber());
    }
}
