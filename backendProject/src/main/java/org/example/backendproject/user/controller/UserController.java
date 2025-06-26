package org.example.backendproject.user.controller;

import org.example.backendproject.user.dto.UserDTO;
import org.example.backendproject.user.service.UserService;
import org.example.backendproject.security.core.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 내 정보 보기 **/
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long id = customUserDetails.getId();
        return ResponseEntity.ok(userService.getMyInfo(id));
    }

    //@AuthenticationPrincipal : 스프링 시큐리티에서 인증된 사용자정보를 자동으로 주입받는 어노테이션
    //요청 헤더 안에 있는 JWT 토큰에서 사용자 정보를 읽어옴

    /** 유저 정보 수정 **/
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserDTO dto)  {
        Long id = customUserDetails.getId();
        UserDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    //dto로 순환참조 방지
    @GetMapping("/profile/{profileId}")
    public UserDTO getProfile(@PathVariable Long profileId){
        return null;
    }


}
