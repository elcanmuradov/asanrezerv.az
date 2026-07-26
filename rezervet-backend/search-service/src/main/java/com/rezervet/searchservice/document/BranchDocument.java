package com.rezervet.searchservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Document(indexName = "branches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String restaurantId;

    @Field(type = FieldType.Keyword)
    private String restaurantName;

    @Field(type = FieldType.Text)
    private String branchName; // Məs: "Nərimanov Filialı"

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String district;

    @Field(type = FieldType.Keyword)
    private String address;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Integer)
    private int minTableCapacity;

    @Field(type = FieldType.Integer)
    private int maxTableCapacity;

    @Field(type = FieldType.Date_Range)
    private String workingHours;

    @Field(type = FieldType.Date)
    private String createdAt;

    @Field(type = FieldType.Date)
    private String updatedAt;


}

/*

private UUID id;

    // Eyni servisdə olduğu üçün əlaqə istifadə edirik.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;
    private String workingHours;   // məs: "10:00 - 23:00"

    private String googleMapsLink;

    private List<String> photosUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


 */