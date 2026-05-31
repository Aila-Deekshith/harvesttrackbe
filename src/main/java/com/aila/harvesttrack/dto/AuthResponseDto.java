package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String  accessToken;     // ← Short lived (15 mins)
    private String  refreshToken;    // ← Long lived  (30 days)
    private Integer ownerId;
    private String  name;
    private String  phone;
    private String  address;
}