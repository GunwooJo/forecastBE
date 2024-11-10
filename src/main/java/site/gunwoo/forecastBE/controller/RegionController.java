package site.gunwoo.forecastBE.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.gunwoo.forecastBE.dto.region.RegionDTO;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.entity.Region;
import site.gunwoo.forecastBE.repository.RegionRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegionController {

    private final RegionRepository regionRepository;

    @GetMapping("/regions")
    public ResponseEntity<ResponseDTO> getAllRegions() {

        try {
            List<Region> regions = regionRepository.findAll();
            if(regions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("서버에 저장된 지역 없음.", null));
            }

            List<RegionDTO> regionDTOS = regions.stream()
                    .map(regionEntity ->
                            RegionDTO.builder()
                                    .r1(regionEntity.getR1())
                                    .r2(regionEntity.getR2())
                                    .r3(regionEntity.getR3())
                                    .xPos(regionEntity.getNx())
                                    .yPos(regionEntity.getNy())
                                    .build()
                    ).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("성공", regionDTOS));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 에러가 발생했습니다: " + e.getMessage(), null));
        }

    }
}
