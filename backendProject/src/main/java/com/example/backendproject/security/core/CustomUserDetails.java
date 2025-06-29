package com.example.backendproject.security.core;

import com.example.backendproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
/* 스프링 시큐리티에서 요구하는 사용자 정보 구조를 구현하는 클래스 */
public class CustomUserDetails implements UserDetails {

    //UserDetails : 사용자 정보를 담는 인터페이스
    //요걸 상속받아서 커스텀으로 쓰는것.
    //로그인한 사용자의 정보를 담아두는 역할

    private final User user;

    @Override       //유저의 권한을 GrantedAuthority타입으로 변환(ROLE_USER, ROLE_ADMIN)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //User의 권한을 반환하는 메서드
        //Collections.singleton : 이 사용자는 한가지 권한만 갖는다는 의미
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    //토큰에서 추출한 사용자 정보의 id를 반환하는 메서드(테이블의 PK값)
    //User 엔티티에서 ID 추출
    public Long getId(){
        return user.getId();
    }

    //User엔티티에서 사용자의 password 반환하는 메서드
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    //중복되지 않는 값(이 사람을 식별 할 수 있는 값)
    @Override
    public String getUsername() {
        return user.getUserid();
    }

//     이런식으로 커스텀 메서드 추가도 가능!
//    public String getUserEmail() {
//        return user.getUserProfile().getEmail();
//    }


    /** 아래는 현재 계정 상태를 판단하는 메서드 **/

    //현재 게정 상태가 활성화된 상태인지(flase : 비활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }

    //현재 이 계정이 만료되었는지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //현재 이 계정이 잠겨있는지
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //현재 이 계정의 자격증명이 만료되지 않았는지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
