package com.studygroup.domain.group.repository;

import com.studygroup.domain.group.entity.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    @Query("""
            SELECT studyGroup
            FROM StudyGroup studyGroup
            WHERE LOWER(studyGroup.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(studyGroup.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(studyGroup.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<StudyGroup> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
