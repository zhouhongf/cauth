package com.myworld.cauth.siteinfo.repository;

import com.myworld.cauth.siteinfo.entity.MyFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MyFileRepository extends JpaRepository<MyFile, Long> {

    MyFile findByIdDetail(String idDetail);
    void deleteByIdDetail(String idDetail);

    MyFile findByOfficialName(String officialName);
    void deleteByOfficialName(String officialName);
}
