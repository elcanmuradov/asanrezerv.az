package com.devjoint.mediaservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {
    private final Cloudinary cloudinary;

    public List<Map<String, String>> uploadFiles(List<MultipartFile> files) {
        log.info("Uploading files to Cloudinary");
        List<Map<String, String>> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (!file.isEmpty()) {
                    Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    results.add(Map.of(
                            "url", String.valueOf(uploadResult.get("secure_url")),
                            "publicId", String.valueOf(uploadResult.get("public_id"))
                    ));
                }
            } catch (IOException e) {
                results.add(Map.of(
                        "error", "Fayl yüklənmədi: " + file.getOriginalFilename(),
                        "fileName", file.getOriginalFilename()
                ));
            }
        }

        log.info("Uploaded files to Cloudinary");
        return results;
    }


    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                File newFile = addWatermark(file);
                Map<?, ?> uploadResult = cloudinary.uploader().upload(newFile, ObjectUtils.emptyMap());
                return Map.of(
                        "url", String.valueOf(uploadResult.get("secure_url")),
                        "publicId", String.valueOf(uploadResult.get("public_id"))
                );
            }
        } catch (IOException e) {
            log.error("Fayl yüklənmədi: " + file.getOriginalFilename(), e);
        }
        return null;
    }

    public void deleteById(String publicId) {
        try{
            cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /*

    public void deleteImages(List<String> publicIds) {
        try {
            if (publicIds != null && !publicIds.isEmpty()) {
                cloudinary.api().deleteResources(publicIds, ObjectUtils.emptyMap());
                log.info("Cloudinary-dən silindi: " + publicIds.size() + " ədəd şəkil.");
            }
        } catch (Exception e) {
            log.error("Resurslar silinərkən xəta: " + e.getMessage());
        }
    }
}

     */


    private File addWatermark(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("Yüklənən fayl etibarlı şəkil deyil!");
        }

        Graphics2D g2d = originalImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int fontSize = Math.max(20, originalImage.getWidth() / 15);
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));

        g2d.setColor(Color.WHITE);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        String watermarkText = "rezervet.az";
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(watermarkText);
        int textHeight = fontMetrics.getHeight();

        int x = originalImage.getWidth() - textWidth - 20;
        int y = originalImage.getHeight() - 20;

        g2d.drawString(watermarkText, x, y);

        g2d.dispose();

        // 8. Nəticəni müvəqqəti fayla yaz (və ya birbaşa S3/Cloud storage-ə göndər)
        String formatName = getFileExtension(file.getOriginalFilename());
        File watermarkedFile = File.createTempFile("watermarked_", "." + formatName);

        ImageIO.write(originalImage, formatName, watermarkedFile);

        return watermarkedFile;
    }

    // Fayl genişlənməsini təyin edən köməkçi metod
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg"; // Default
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }


}

