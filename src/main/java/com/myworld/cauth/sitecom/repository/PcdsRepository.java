package com.myworld.cauth.sitecom.repository;

import com.myworld.cauth.sitecom.entity.Pcds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface PcdsRepository extends JpaRepository<Pcds, Long> {

    Pcds findByFullname(String fullname);
    List<Pcds> findByNameAndLevel(String name, String level);

}
