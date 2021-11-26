package com.myworld.cauth.siteinfo.repository;


import com.myworld.cauth.siteinfo.entity.SmallPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SmallPicRepository extends JpaRepository<SmallPic, Long> {

    SmallPic findByIdDetail(String idDetail);
    void deleteByIdDetail(String idDetail);
}
