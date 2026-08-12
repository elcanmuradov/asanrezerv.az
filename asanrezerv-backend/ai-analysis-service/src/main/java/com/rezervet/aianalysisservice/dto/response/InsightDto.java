package com.rezervet.aianalysisservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

// Level 2 tövsiyələr + (varsa) LLM narrative
@Data
@Builder
@AllArgsConstructor
public class InsightDto {
    private String narrative;            // təbii dildə hesabat (LLM və ya qayda-əsaslı)
    private boolean llmGenerated;        // narrative LLM ilə yaradılıb?
    private List<String> recommendations; // qayda-əsaslı konkret tövsiyələr
}
