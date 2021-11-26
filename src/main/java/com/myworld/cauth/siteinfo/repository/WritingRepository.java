package com.myworld.cauth.siteinfo.repository;

import com.myworld.cauth.siteinfo.entity.Writing;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface WritingRepository extends JpaRepository<Writing, Long> {

    Writing findByIdDetail(String idDetail);
    void deleteByIdDetail(String idDetail);

    List<Writing> findByType(String type, Sort sort);
    List<Writing> findByTypeAndCanRelease(String type, String canRelease);

    Writing findByTitleAndCanRelease(String title, String canRelease);
    Writing findByTitle(String title);
}
