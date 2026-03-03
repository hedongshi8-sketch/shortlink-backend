package com.shortlink.controller;

import com.shortlink.entity.UrlEntity;
import com.shortlink.repository.UrlRepository;
import com.shortlink.utils.ShortCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UrlController {

    @Autowired
    private UrlRepository urlRepository;

    // 创建短链接
    @PostMapping("/api/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody UrlRequest request) {
        String originalUrl = request.getUrl();
        if (originalUrl == null || originalUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("URL is required");
        }

        // 检查URL是否已存在
        UrlEntity existingUrl = urlRepository.findByOriginalUrl(originalUrl);
        if (existingUrl != null) {
            return ResponseEntity.ok(new UrlResponse(
                    existingUrl.getOriginalUrl(),
                    "http://localhost:8080/" + existingUrl.getShortCode(),
                    existingUrl.getShortCode()
            ));
        }

        // 生成新的短码
        String shortCode = ShortCodeGenerator.generateShortCode(originalUrl);
        // 确保短码唯一
        while (urlRepository.findByShortCode(shortCode) != null) {
            shortCode = ShortCodeGenerator.generateShortCode(originalUrl + System.currentTimeMillis());
        }

        // 创建新的URL实体
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setShortCode(shortCode);
        urlEntity.setOriginalUrl(originalUrl);
        urlEntity.setCreatedAt(LocalDateTime.now());
        urlEntity.setClicks(0);
        urlEntity.setStatus(true);

        urlRepository.save(urlEntity);

        return ResponseEntity.ok(new UrlResponse(
                originalUrl,
                "http://localhost:8080/" + shortCode,
                shortCode
        ));
    }

    // 重定向短链接
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginal(@PathVariable String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode);
        if (urlEntity == null || !urlEntity.isStatus()) {
            return new RedirectView("/error");
        }

        // 增加点击次数
        urlEntity.setClicks(urlEntity.getClicks() + 1);
        urlRepository.save(urlEntity);

        return new RedirectView(urlEntity.getOriginalUrl());
    }

    // 获取短链接信息
    @GetMapping("/api/info/{shortCode}")
    public ResponseEntity<?> getUrlInfo(@PathVariable String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode);
        if (urlEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short code not found");
        }

        return ResponseEntity.ok(urlEntity);
    }

    // 获取所有短链接
    @GetMapping("/api/urls")
    public ResponseEntity<List<UrlEntity>> getAllUrls() {
        return ResponseEntity.ok(urlRepository.findAll());
    }

    // 更新短链接
    @PutMapping("/api/update/{shortCode}")
    public ResponseEntity<?> updateUrl(@PathVariable String shortCode, @RequestBody UrlRequest request) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode);
        if (urlEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short code not found");
        }

        urlEntity.setOriginalUrl(request.getUrl());
        urlRepository.save(urlEntity);

        return ResponseEntity.ok(urlEntity);
    }

    // 更新短链接状态
    @PutMapping("/api/status/{shortCode}")
    public ResponseEntity<?> updateUrlStatus(@PathVariable String shortCode, @RequestBody StatusRequest request) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode);
        if (urlEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short code not found");
        }

        urlEntity.setStatus(request.getStatus());
        urlRepository.save(urlEntity);

        return ResponseEntity.ok(urlEntity);
    }

    // 删除短链接
    @DeleteMapping("/api/delete/{shortCode}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode);
        if (urlEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short code not found");
        }

        urlRepository.delete(urlEntity);

        return ResponseEntity.ok("Short link deleted successfully");
    }

    // 请求和响应类
    static class UrlRequest {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    static class StatusRequest {
        private boolean status;

        public boolean getStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }

    static class UrlResponse {
        private String original_url;
        private String short_url;
        private String short_code;

        public UrlResponse(String original_url, String short_url, String short_code) {
            this.original_url = original_url;
            this.short_url = short_url;
            this.short_code = short_code;
        }

        public String getOriginal_url() {
            return original_url;
        }

        public void setOriginal_url(String original_url) {
            this.original_url = original_url;
        }

        public String getShort_url() {
            return short_url;
        }

        public void setShort_url(String short_url) {
            this.short_url = short_url;
        }

        public String getShort_code() {
            return short_code;
        }

        public void setShort_code(String short_code) {
            this.short_code = short_code;
        }
    }
}