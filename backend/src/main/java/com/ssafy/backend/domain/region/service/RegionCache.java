package com.ssafy.backend.domain.region.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.region.entity.Gugun;
import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.region.repository.GugunRepository;
import com.ssafy.backend.domain.region.repository.SidoRepository;

import lombok.RequiredArgsConstructor;

// 시도·구군은 거의 바뀌지 않는 참조 데이터이므로 처음 조회할 때 한 번 읽어 메모리에서 재사용한다.
// (모임 목록에서 모임마다 지역을 조회하던 N+1 쿼리를 없앤다)
// 캐시에 없는 코드는 저장소를 직접 조회한다. 운영 중에 지역이 추가되어도 잘못된 결과를 주지 않기 위해서다.
@Component
@RequiredArgsConstructor
public class RegionCache {
    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;

    private volatile Snapshot snapshot;

    public Sido getSido(Integer sidoCode) {
        return findSido(sidoCode).orElseThrow(() -> new IllegalArgumentException("시도 정보를 찾을 수 없습니다."));
    }

    public Optional<Sido> findSido(Integer sidoCode) {
        Sido sido = snapshot().sidos().get(sidoCode);
        return sido != null ? Optional.of(sido) : sidoRepository.findById(sidoCode);
    }

    public Gugun getGugun(Integer sidoCode, Integer gugunCode) {
        Gugun gugun = snapshot().guguns().get(new GugunKey(sidoCode, gugunCode));
        if (gugun != null) {
            return gugun;
        }
        return gugunRepository.findBySidoCodeAndGugunCode(sidoCode, gugunCode).orElseThrow(
                () -> new IllegalArgumentException("구군 정보를 찾을 수 없습니다."));
    }

    public List<Sido> getAllSidos() {
        return List.copyOf(snapshot().sidos().values());
    }

    public List<Gugun> getGugunsBySido(Integer sidoCode) {
        List<Gugun> guguns = snapshot().gugunsBySido().get(sidoCode);
        return guguns != null ? guguns : gugunRepository.findBySidoCode(sidoCode);
    }

    private Snapshot snapshot() {
        Snapshot loaded = snapshot;
        if (loaded == null) {
            synchronized (this) {
                loaded = snapshot;
                if (loaded == null) {
                    loaded = load();
                    snapshot = loaded;
                }
            }
        }
        return loaded;
    }

    private Snapshot load() {
        Map<Integer, Sido> sidos = sidoRepository.findAll().stream().collect(
                Collectors.toMap(Sido::getSidoCode, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        List<Gugun> allGuguns = gugunRepository.findAll();
        Map<GugunKey, Gugun> guguns = allGuguns.stream().collect(
                Collectors.toUnmodifiableMap(g -> new GugunKey(g.getSidoCode(), g.getGugunCode()), Function.identity(),
                                             (a, b) -> a));
        Map<Integer, List<Gugun>> gugunsBySido = allGuguns.stream().collect(
                Collectors.groupingBy(Gugun::getSidoCode, LinkedHashMap::new,
                                      Collectors.collectingAndThen(Collectors.toList(), List::copyOf)));
        return new Snapshot(sidos, guguns, gugunsBySido);
    }

    private record GugunKey(Integer sidoCode, Integer gugunCode) {
    }

    private record Snapshot(Map<Integer, Sido> sidos, Map<GugunKey, Gugun> guguns,
                            Map<Integer, List<Gugun>> gugunsBySido) {
    }
}
