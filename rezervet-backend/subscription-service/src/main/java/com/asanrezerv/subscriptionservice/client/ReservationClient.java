package com.asanrezerv.subscriptionservice.client;

import com.asanrezerv.subscriptionservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "reservation-service", url = "${spring.client.urls.reservation-service}/api/reservations")
public interface ReservationClient {

    @GetMapping("/count/monthly")
    ApiResponse<Long> countThisMonth();
}
