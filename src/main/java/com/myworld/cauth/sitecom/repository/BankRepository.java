package com.myworld.cauth.sitecom.repository;

import com.myworld.cauth.sitecom.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    List<Bank> findByCityname(String cityname);
    List<Bank> findByCitynameStartingWith(String cityname);

    Optional<List<Bank>> findByCitynameAndParentName(String cityname, String parentName);

    Optional<List<Bank>> findByCitynameStartsWithAndParentName(String cityname, String parentName);
}
