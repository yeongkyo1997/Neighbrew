package com.ssafy.backend.domain.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.common.auth.LoginUserGuard;
import com.ssafy.backend.authentication.application.LoginResponse;
import com.ssafy.backend.common.enums.UploadType;
import com.ssafy.backend.common.service.JwtService;
import com.ssafy.backend.domain.storage.service.s3.S3Service;
import com.ssafy.backend.domain.user.dto.UserResponseDto;
import com.ssafy.backend.domain.user.dto.UserUpdateDto;
import com.ssafy.backend.domain.user.service.UserService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final S3Service s3Service;
    private final JwtService jwtService;

    //전체 유저 검색
    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @GetMapping("/myinfo")
    public ResponseEntity<UserResponseDto> getMyInfo(@LoginUser Long userId) {
        return ResponseEntity.ok().body(userService.findByUserId(userId));
    }

    // userId로 유저 검색
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.findByUserId(userId));
    }

    // refresh token을 이용한 access token 재발급
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody LoginResponse refreshToken) {
        Claims claims = jwtService.getClaimsFromToken(refreshToken.getRefreshToken());
        String userId = claims.getSubject();
        Map<String, String> tokens = jwtService.generateTokens(userId);
        return ResponseEntity.ok(tokens);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserInfo(@RequestBody UserUpdateDto userUpdateDto,
                                                          @PathVariable Long userId,
                                                          @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        return ResponseEntity.ok(userService.updateUser(userId, userUpdateDto));
    }

    @PutMapping("/img/{userId}")
    public ResponseEntity<String> updateUserImg(@RequestParam("profile") Optional<MultipartFile> profile,
                                                @PathVariable Long userId,
                                                @LoginUser Long loginUserId) throws IOException {
        LoginUserGuard.requireSame(userId, loginUserId);
        String url = profile.isPresent() ? Objects.equals(profile.get().getOriginalFilename(), "") ?
                                           "no image" : s3Service.upload(UploadType.USERPROFILE,
                                                                         profile.get()) : "no image";

        userService.updateUserImg(userId, url);
        return ResponseEntity.ok(url);
    }


    @GetMapping("/search/{nickname}")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.searchUsers(nickname));
    }
}
