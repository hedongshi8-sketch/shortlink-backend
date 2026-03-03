package com.shortlink.repository;

import com.shortlink.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    UrlEntity findByShortCode(String shortCode);
    UrlEntity findByOriginalUrl(String originalUrl);
}