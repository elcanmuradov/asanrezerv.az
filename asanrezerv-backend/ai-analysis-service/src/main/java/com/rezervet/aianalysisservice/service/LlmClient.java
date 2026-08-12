package com.rezervet.aianalysisservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * DeepSeek Chat Completions API inteqrasiyası (OpenAI-uyğun format) — yalnız Level 3
 * "dərin AI analiz" üçün. Key/enabled yoxdursa isEnabled() false qaytarır və çağıran
 * qayda-əsaslı mətnə keçir.
 */
@Slf4j
@Component
public class LlmClient {

    @Value("${ai.llm.enabled:false}")
    private boolean enabled;

    @Value("${ai.llm.api-key:}")
    private String apiKey;

    @Value("${ai.llm.model:deepseek-chat}")
    private String model;

    @Value("${ai.llm.base-url:https://api.deepseek.com/chat/completions}")
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
                    .header("Authorization", "Bearer " + apiKey)
                    .header("content-type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            // response.choices[0].message.content
            if (resp != null && resp.get("choices") instanceof List<?> choices && !choices.isEmpty()
                    && choices.get(0) instanceof Map<?, ?> first
                    && first.get("message") instanceof Map<?, ?> message) {
                Object text = message.get("content");
                return text == null ? null : text.toString();
            }
        } catch (Exception e) {
            log.warn("LLM çağırışı uğursuz oldu, qayda-əsaslı mətnə keçilir: {}", e.getMessage());
        }
        return null;
    }
}
