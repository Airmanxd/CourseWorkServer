package com.example.coursework.repositories;

import com.example.coursework.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByVideoId(Long videoId);
    Video findByName(String name);
    List<Video> findByCategory(String category);
}
