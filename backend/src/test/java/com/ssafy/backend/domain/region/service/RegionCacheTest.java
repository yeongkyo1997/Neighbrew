package com.ssafy.backend.domain.region.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.backend.domain.region.entity.Gugun;
import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.region.repository.GugunRepository;
import com.ssafy.backend.domain.region.repository.SidoRepository;

@ExtendWith(MockitoExtension.class)
class RegionCacheTest {
    @Mock
    private SidoRepository sidoRepository;
    @Mock
    private GugunRepository gugunRepository;

    @InjectMocks
    private RegionCache regionCache;

    private final Sido seoul = sido(11);
    private final Sido busan = sido(26);
    private final Gugun jongno = gugun(11, 110, "종로구");
    private final Gugun junggu = gugun(11, 140, "중구");
    private final Gugun haeundae = gugun(26, 350, "해운대구");

    private static Sido sido(int code) {
        Sido sido = mock(Sido.class);
        given(sido.getSidoCode()).willReturn(code);
        return sido;
    }

    private static Gugun gugun(int sidoCode, int gugunCode, String name) {
        return Gugun.builder().sidoCode(sidoCode).gugunCode(gugunCode).gugunName(name).build();
    }

    @BeforeEach
    void setUp() {
        given(sidoRepository.findAll()).willReturn(List.of(seoul, busan));
        given(gugunRepository.findAll()).willReturn(List.of(jongno, junggu, haeundae));
    }

    @Test
    void 여러_번_조회해도_저장소는_처음_한_번만_읽는다() {
        for (int i = 0; i < 20; i++) {
            regionCache.getSido(11);
            regionCache.getGugun(11, 110);
        }

        then(sidoRepository).should(times(1)).findAll();
        then(gugunRepository).should(times(1)).findAll();
    }

    @Test
    void 코드로_시도와_구군을_찾는다() {
        assertThat(regionCache.getSido(26)).isSameAs(busan);
        assertThat(regionCache.getGugun(11, 140)).isSameAs(junggu);
        assertThat(regionCache.getAllSidos()).containsExactly(seoul, busan);
    }

    @Test
    void 시도별_구군_목록을_돌려준다() {
        assertThat(regionCache.getGugunsBySido(11)).containsExactly(jongno, junggu);
        assertThat(regionCache.getGugunsBySido(26)).containsExactly(haeundae);
    }

    @Test
    void 캐시에_없는_코드는_저장소를_직접_조회한다() {
        Sido sejong = mock(Sido.class);
        given(sidoRepository.findById(36)).willReturn(Optional.of(sejong));

        assertThat(regionCache.getSido(36)).isSameAs(sejong);
    }

    @Test
    void 저장소에도_없으면_예외를_던진다() {
        given(sidoRepository.findById(99)).willReturn(Optional.empty());
        given(gugunRepository.findBySidoCodeAndGugunCode(11, 999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> regionCache.getSido(99)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> regionCache.getGugun(11, 999)).isInstanceOf(IllegalArgumentException.class);
        assertThat(regionCache.findSido(99)).isEmpty();
    }
}
