package com.rezervet.aianalysisservice.client;

import com.rezervet.aianalysisservice.dto.ApiResponse;
import com.rezervet.aianalysisservice.dto.source.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "reservation-service", url = "${spring.client.urls.reservation-service}/api/reservations")
public interface ReservationClient {

    @GetMapping("/restaurant/{id}")
    ApiResponse<List<ReservationDto>> getByRestaurant(@PathVariable UUID id);

    @GetMapping("/branch/{id}")
    ApiResponse<List<ReservationDto>> getByBranch(@PathVariable UUID id);
}
