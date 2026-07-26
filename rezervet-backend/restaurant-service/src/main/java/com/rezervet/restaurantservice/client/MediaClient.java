package com.rezervet.restaurantservice.client;

import com.rezervet.restaurantservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "media-service", url = "${spring.services.media-service.url}/api/media")
public interface MediaClient {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Map<String, String>> upload(@RequestPart("file") MultipartFile file);

    @DeleteMapping("/delete")
    void deleteById(@RequestParam("publicId") String publicId);

}
