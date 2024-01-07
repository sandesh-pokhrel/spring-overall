package com.sandesh.overall.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class VideoGame {

    @Id
    private Long id;
    private String name;
    private String platform;
    private Integer year;
    private String genre;
    private String publisher;
    private Float naSales;
    private Float euSales;
    private Float jpSales;
    private Float otherSales;
    private Float globalSales;
}
