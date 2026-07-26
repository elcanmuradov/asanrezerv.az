package com.rezervet.aianalysisservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Claude Messages API (Anthropic) inteqrasiyası — yalnız Level 2 narrative üçün.
 * Key/enabled yoxdursa isEnabled() false qaytarır və çağıran qayda-əsaslı mətnə keçir.
 */
@Slf4j
@Component
public class LlmClient {

    @Value("${ai.llm.enabled:false}")
    private boolean enabled;

    @Value("${ai.llm.api-key:}")
    private String apiKey;

    @Value("${ai.llm.model:claude-haiku-4-5}")
    private String model;

    @Value("${ai.llm.base-url:https://api.anthropic.com/v1/messages}")
    private String baseUrl;

    private final RestClient http = RestClient.create();

    public boolean isEnabled() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }

    /** Prompt-a əsasən narrative qaytarır; xəta olarsa null (fallback üçün). */
    public String generate(String prompt) {
        if (!isEnabled()) return null;
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", 700,
                    "messages", List.of(Map.of("role", "user", "content", prompt))
            );
            Map<?, ?> resp = http.post()
                    .uri(baseUrl)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            // response.content[0].text
            if (resp != null && resp.get("content") instanceof List<?> content && !content.isEmpty()
                    && content.get(0) instanceof Map<?, ?> first) {
                Object text = first.get("text");
                return text == null ? null : text.toString();
            }
        } catch (Exception e) {
            log.warn("LLM çağırışı uğursuz oldu, qayda-əsaslı mətnə keçilir: {}", e.getMessage());
        }
        return null;
    }
}
