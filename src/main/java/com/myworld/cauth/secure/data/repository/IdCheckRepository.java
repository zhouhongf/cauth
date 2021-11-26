package com.myworld.cauth.secure.data.repository;


import com.myworld.cauth.secure.data.entity.IdCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Transactional
@Repository
public interface IdCheckRepository extends JpaRepository<IdCheck, Long> {

    Long countByPlayerTypeAndAlreadySetup(String playerType, String alreadySetup);
    Page<IdCheck> findByPlayerType(String playerType, Pageable pageable);
    IdCheck findByIdDetailAndIdCheckApplyTime(String idDetail, Timestamp idCheckApplyTime);

}
