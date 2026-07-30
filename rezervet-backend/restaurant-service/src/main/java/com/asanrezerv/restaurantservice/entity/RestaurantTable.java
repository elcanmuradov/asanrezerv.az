package com.asanrezerv.restaurantservice.entity;

import com.asanrezerv.restaurantservice.enums.TableStatus;
import com.asanrezerv.restaurantservice.enums.TableType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "restaurant_tables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;

    private String zone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TableStatus status = TableStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    private TableType type ;


}