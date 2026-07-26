package com.devjoint.mediaservice.controller;

import com.devjoint.mediaservice.dto.ApiResponse;
import com.devjoint.mediaservice.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;


    @PostMapping("/upload-multiple")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> uploadMultipleFiles(
            @RequestPart("files") List<MultipartFile> files) {

        return ResponseEntity.ok(ApiResponse.success(mediaService.uploadFiles(files)));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.uploadFile(file)));
    }

    @DeleteMapping("/delete")
    public void deleteById(@RequestParam("publicId") String publicId) {
        mediaService.deleteById(publicId);
    }


}
