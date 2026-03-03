package com.shortlink.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ShortCodeGenerator {
    private static final String BASE62_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    public static String generateShortCode(String originalUrl) {
        try {
            // 使用MD5哈希URL
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(originalUrl.getBytes());
            
            // 将哈希值转换为Base62
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                int index = Math.abs(hash[i % hash.length]) % BASE62_CHARS.length();
                sb.append(BASE62_CHARS.charAt(index));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果哈希算法失败，使用随机生成
            return generateRandomShortCode();
        }
    }

    private static String generateRandomShortCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(BASE62_CHARS.charAt(random.nextInt(BASE62_CHARS.length())));
        }
        return sb.toString();
    }
}