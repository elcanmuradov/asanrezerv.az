package com.asanrezerv.mediaservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
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
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
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



}

