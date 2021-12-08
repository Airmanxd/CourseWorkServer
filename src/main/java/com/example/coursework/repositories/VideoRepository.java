package com.example.coursework.repositories;

import com.example.coursework.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByVideoId(Long videoId);
    Video findByName(String name);
    List<Video> findByCategory(String category);
}
