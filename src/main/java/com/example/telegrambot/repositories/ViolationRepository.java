package com.example.telegrambot.repositories;

import com.example.telegrambot.entity.Photo;
import com.example.telegrambot.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    @Query(value = "select ph.* from violation v " +
            "inner join photo ph on ph.violation_id=v.id " +
            "where violation_id =?1" ,nativeQuery = true)
    Photo getPhotoByViolationId(Long violation_id);
}
