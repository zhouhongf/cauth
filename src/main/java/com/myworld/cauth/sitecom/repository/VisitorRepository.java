package com.myworld.cauth.sitecom.repository;

import com.myworld.cauth.sitecom.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Visitor findByIdDetail(String idDetail);
}
