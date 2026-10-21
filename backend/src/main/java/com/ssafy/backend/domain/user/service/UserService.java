package com.ssafy.backend.domain.user.service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
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
    private static final Pattern MASKED_NICKNAME = Pattern.compile("user_\\d+", Pattern.CASE_INSENSITIVE);
    // 공백·제로폭 문자 등 화면에 보이지 않는 문자
    private static final Pattern INVISIBLE_CHARS = Pattern.compile("[\\s\\p{Z}\\p{Cc}\\p{Cf}]");

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

        // 이메일 닉네임인 기존 사용자가 닉네임은 두고 다른 항목만 고치는 경우는 허용한다.
        if (!updateDto.getNickname().equals(user.getNickname())) {
            validateNewNickname(updateDto.getNickname());
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

    // 이메일은 닉네임으로 쓸 수 없고, 이메일 닉네임 사용자를 가린 공개 이름(user_{id})은 남이 가져갈 수 없다.
    // 전각 문자·대소문자·보이지 않는 문자로 우회하지 못하도록 화면에 보이는 형태로 정규화해서 검사한다.
    private void validateNewNickname(String nickname) {
        String visible = INVISIBLE_CHARS.matcher(Normalizer.normalize(nickname, Normalizer.Form.NFKC)).replaceAll("");
        if (visible.contains("@")) {
            throw new IllegalArgumentException("닉네임에 '@'는 사용할 수 없습니다.");
        }
        if (MASKED_NICKNAME.matcher(visible).matches()) {
            throw new IllegalArgumentException("user_숫자 형식의 닉네임은 사용할 수 없습니다.");
        }
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
        return userRepository.findByNicknameContainingAndNicknameNotContaining(nickName, "@").stream()
                             .map(PublicUserResponseDto::fromEntity).collect(Collectors.toList());
    }
}
