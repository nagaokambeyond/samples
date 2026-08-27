package com.example.javaobjectmapper.mapper;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LombokMappedPerson {

    private Long id;

    private Long sourceId;

    private String fullName;

    private Integer age;

    private String ageGroup;

    private String email;

    private String addressLine;

    private Integer loyaltyPoints;

    private LocalDateTime createdAt;
}
