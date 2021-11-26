package com.myworld.cauth.sitecom.repository;

import com.myworld.cauth.sitecom.entity.BankLite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface BankLiteRepository extends JpaRepository<BankLite, Long> {

    List<BankLite> findByCity(String city);
}
