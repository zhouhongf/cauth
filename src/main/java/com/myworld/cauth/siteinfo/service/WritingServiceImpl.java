package com.myworld.cauth.siteinfo.service;


import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.siteinfo.entity.TinymcePhoto;
import com.myworld.cauth.siteinfo.entity.Writing;
import com.myworld.cauth.siteinfo.repository.SmallPicRepository;
import com.myworld.cauth.siteinfo.repository.TinymcePhotoRepository;
import com.myworld.cauth.siteinfo.repository.WritingRepository;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.TitleContent;
import com.myworld.cauth.common.UtilService;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;


@Service
public class WritingServiceImpl implements WritingService {
    private static Logger log = LogManager.getRootLogger();

    private WritingRepository writingRepository;
    private SmallPicRepository smallPicRepository;
    private TinymcePhotoRepository tinymcePhotoRepository;
    private UtilService utilService;

    @Autowired
    public WritingServiceImpl(
            WritingRepository writingRepository,
            SmallPicRepository smallPicRepository,
            TinymcePhotoRepository tinymcePhotoRepository,
            UtilService utilService
    ){
        this.writingRepository = writingRepository;
        this.smallPicRepository = smallPicRepository;
        this.tinymcePhotoRepository = tinymcePhotoRepository;
        this.utilService = utilService;
    }

    @Override
    public ApiResult adminGetWritingList(String type){
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<Writing> writings = writingRepository.findByType(type, sort);
        List<Map> writingsSimple = new ArrayList<>();
        for (Writing writing : writings) {
            Map<String, Object> map = new HashMap<>();
            map.put("idDetail", writing.getIdDetail());
            map.put("title", writing.getTitle());
            map.put("readNums", writing.getReadNums());
            map.put("createTime", Long.toString(writing.getCreateTime().getTime()));
            map.put("canRelease", writing.getCanRelease());
            writingsSimple.add(map);
        }
        return ResultUtil.success(writingsSimple);
    }

    @Override
    public ApiResult adminCreateWriting(String type, String title) {
        SysAdmin userDetail = (SysAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String adminIdDetail = userDetail.getIdDetail();
        Writing write = writingRepository.findByTitle(title);
        if (write != null) {
            return ResultUtil.error(-2, "已存在相同标题的文章");
        }

        Writing writing = new Writing();
        String theIdDetail = "W" + new Date().getTime();
        writing.setIdDetail(theIdDetail);
        writing.setReadNums(1);
        writing.setCanRelease("NO");
        writing.setType(type);
        writing.setTitle(title);
        writing.setCreator(adminIdDetail);
        writingRepository.save(writing);
        return ResultUtil.success();
    }

    @Override
    public ApiResult adminGetWriting(String idDetail) {
        Writing writing = writingRepository.findByIdDetail(idDetail);
        return ResultUtil.success(writing);
    }

    @Override
    public ApiResult adminSetWriting(Writing writing) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String idDetail = writing.getIdDetail();
        Writing wrt = writingRepository.findByIdDetail(idDetail);
        if (wrt == null) {
            return ResultUtil.error(-2, "未能找到相关文章");
        }
        String canRelease = writing.getCanRelease();

        wrt.setTitle(writing.getTitle());
        wrt.setShortInfo(writing.getShortInfo());
        wrt.setAuthor(writing.getAuthor());
        wrt.setType(writing.getType());
        wrt.setSmallPic(writing.getSmallPic());
        wrt.setContent(writing.getContent());
        wrt.setCanRelease(canRelease);
        wrt.setUpdateTime(timestamp);

        if (canRelease.equals("YES")){
            wrt.setReleaseTime(timestamp);

            //从tinymce中提取出图片的链接地址
            Set<String> pics = utilService.getImgStr(wrt.getContent());
            this.setOnUseTinymcePhoto(pics);
            tinymcePhotoRepository.deleteAllByUsageIdDetailAndOnUse(idDetail, "NO");
            log.info("【已全部删除文章idDetail为" + idDetail + "项下未使用的tinymce图片】");
        }
        writingRepository.save(wrt);
        return ResultUtil.success();
    }


    /**
     * 将tinymce中的图片onUse设置为YES
     */
    public void setOnUseTinymcePhoto(Set<String> pics) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for (String p : pics) {
            String temp = p.substring(7);
            String[] strList = temp.split("/");
            int index = strList.length - 1;
            String theIdDetail = strList[index];
            log.info("tinymce中的图片的idDetail是：{}", theIdDetail);
            //截取出图片的idDetail，顺便将该图片的onUse设置为YES
            TinymcePhoto tinymcePhoto = tinymcePhotoRepository.findByIdDetail(theIdDetail);
            tinymcePhoto.setOnUse("YES");
            tinymcePhoto.setUpdateTime(timestamp);
            tinymcePhotoRepository.save(tinymcePhoto);
        }
    }

    @Override
    public ApiResult adminDelWriting(String idDetail){
        writingRepository.deleteByIdDetail(idDetail);
        smallPicRepository.deleteByIdDetail(idDetail);
        tinymcePhotoRepository.deleteAllByUsageIdDetail(idDetail);
        return ResultUtil.success();
    }





    @Override
    public ApiResult getSiteInfo() {
        List<Writing> writings = writingRepository.findByTypeAndCanRelease("规则协议", "YES");
        List<TitleContent> titleContents = new ArrayList<>();
        for (Writing w : writings) {
            TitleContent tc = new TitleContent();
            tc.setTitle(w.getTitle());
            tc.setContent(w.getContent());
            tc.setCreateTime(w.getReleaseTime());
            tc.setIdDetail(w.getIdDetail());

            titleContents.add(tc);
        }
        return ResultUtil.success(titleContents);
    }

    @Override
    public ApiResult getSiteInfoReleaseTime(String title) {
        Writing writing = writingRepository.findByTitleAndCanRelease(title, "YES");
        Timestamp timestamp = writing.getReleaseTime();
        long time = timestamp.getTime();
        return ResultUtil.success(time);
    }

}
