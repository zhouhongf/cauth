package com.myworld.cauth.siteinfo.repository;

import com.myworld.cauth.siteinfo.entity.TinymcePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface TinymcePhotoRepository extends JpaRepository<TinymcePhoto, Long> {

    TinymcePhoto findByIdDetail(String idDetail);
    void deleteByIdDetail(String idDetail);

    List<TinymcePhoto> findByUsageIdDetail(String usageIdDetail);
    void deleteAllByUsageIdDetail(String usageIdDetail);
    void deleteAllByUsageIdDetailAndOnUse(String usageIdDetail, String onUse);
}
