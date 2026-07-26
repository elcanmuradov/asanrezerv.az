package com.rezervet.searchservice.document;

import com.rezervet.searchservice.dto.kafka.restaurant.MediaAssetsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "restaurants")
@Setting(shards = 2, replicas = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantDocument {

        @Id
        private String id;

        @Field(type = FieldType.Keyword)
        private String ownerId;

        @Field(type = FieldType.Keyword)
        private String name;

        @Field(type = FieldType.Keyword)
        private String cuisine;

        @Field(type = FieldType.Keyword)
        private String city;

        @Field(type = FieldType.Keyword)
        private String description;

        @Field(type = FieldType.Keyword)
        private String phone;

        @Field(type = FieldType.Keyword)
        private String bannerUrl;

        @Field(type = FieldType.Keyword)
        private String profilePhotoUrl;

        @Field(type = FieldType.Keyword)
        private List<String> galleryUrls;

        @Field(type = FieldType.Keyword)
        private List<String> menuImages;

        @Field(type = FieldType.Date)
        private LocalDateTime createdAt;

        @Field(type = FieldType.Date)
        private LocalDateTime updatedAt;

        @Field(type = FieldType.Integer)
        @Builder.Default
        private Integer visibilityLevel = 0;

        @Field(type = FieldType.Integer)
        @Builder.Default
        private Integer aiAnalysisLevel = 0;
}