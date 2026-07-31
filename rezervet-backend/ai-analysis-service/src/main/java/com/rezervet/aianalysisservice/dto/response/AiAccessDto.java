package com.rezervet.aianalysisservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

// Frontend nəyi göstərəcəyini bilsin deyə: səviyyə + açıq feature-lər
@Data
@Builder
@AllArgsConstructor
public class AiAccessDto {
    private int level;              // 0 / 1 / 2 / 3
    private boolean canBasic;       // level >= 1
    private boolean canAdvanced;    // level >= 2
    private boolean canDeep;        // level >= 3 (DeepSeek ilə dərin analiz)
    private List<String> features;  // aktiv feature adları
}
