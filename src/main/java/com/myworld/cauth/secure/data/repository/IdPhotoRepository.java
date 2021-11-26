package com.myworld.cauth.secure.data.repository;


import com.myworld.cauth.secure.data.entity.IdPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface IdPhotoRepository extends JpaRepository<IdPhoto, Long>{

    IdPhoto findByUserIdDetailAndPhotoName(String userIdDetail, String photoName);
    void deleteByUserIdDetailAndPhotoName(String userIdDetail, String photoName);

    IdPhoto findByIdDetail(String idDetail);

    void deleteAllByUserIdDetail(String userIdDetail);
}
