package com.ssafy.backend.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.backend.domain.storage.service.s3.S3Service;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;
import com.ssafy.backend.domain.user.dto.UserResponseDto;
import com.ssafy.backend.domain.user.dto.UserUpdateDto;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public PublicUserResponseDto findByUserId(Long userId) {
        return PublicUserResponseDto.fromEntity(findUser(userId));
    }

    // email·name·birth를 포함하므로 본인 조회에만 쓴다.
    public UserResponseDto findMyInfo(Long userId) {
        return UserResponseDto.fromEntity(findUser(userId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto updateDto) {
        User user = findUser(userId);

        // 이메일을 닉네임으로 쓰지 못하게 한다. (이메일 닉네임인 기존 사용자가 다른 항목만 고치는 경우는 허용)
        if (!updateDto.getNickname().equals(user.getNickname()) && updateDto.getNickname().contains("@")) {
            throw new IllegalArgumentException("닉네임에 '@'는 사용할 수 없습니다.");
        }

        boolean nicknameExists = userRepository.existsByNickname(updateDto.getNickname());
        if (nicknameExists && !updateDto.getNickname().equals(
                user.getNickname())) {
            throw new IllegalArgumentException("닉네임 중복");
        }

        if (!nicknameExists && updateDto.getNickname().equals(
                user.getNickname()) && updateDto.getBirth().equals(user.getBirth()) && updateDto.getIntro()
                                                                                                .equals(user.getIntro())) {
            throw new IllegalArgumentException("변경사항이 없습니다.");
        }

        user.updateFromDto(updateDto);
        return UserResponseDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void updateUserImg(Long userId, String url) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));

        //프로필이 존재하면 기존 이미지 제거한다.
        if (!user.getProfile().equals("no image")) {s3Service.deleteImg(user.getProfile());}

        user.updateImg(url);
        userRepository.save(user);
    }

    public List<PublicUserResponseDto> findAll() {
        return userRepository.findAll().stream().map(PublicUserResponseDto::fromEntity).collect(Collectors.toList());
    }

    public List<PublicUserResponseDto> searchUsers(String nickName) {
        return userRepository.findByNicknameContaining(nickName).stream().map(PublicUserResponseDto::fromEntity)
                             .collect(Collectors.toList());
    }
}
