package com.example.backendproject.Auth.dto;

import com.example.backendproject.Auth.entity.Auth;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long userId;

    @Builder
    public LoginResponseDTO(Auth auth){
        this.tokenType = auth.getTokenType();
        this.accessToken = auth.getAccessToken();
        this.refreshToken = auth.getRefreshToken();
        this.userId = auth.getId();
    }
}
