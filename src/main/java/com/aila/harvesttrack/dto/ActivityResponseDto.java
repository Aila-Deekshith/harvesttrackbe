package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDto {
    private Integer id;
    private String activityName;
    private String cropType;
}
