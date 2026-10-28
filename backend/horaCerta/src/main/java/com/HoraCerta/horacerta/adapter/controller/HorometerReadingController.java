package com.HoraCerta.horacerta.adapter.controller;

import com.HoraCerta.horacerta.adapter.dto.request.horometerreading.HorometerReadingRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.HorometerReadingResponseDTO;
import com.HoraCerta.horacerta.domain.service.HorometerReadingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/horometer-readings")
public class HorometerReadingController {
    private final HorometerReadingService horometerReadingService;

    public HorometerReadingController(HorometerReadingService horometerReadingService) {
        this.horometerReadingService = horometerReadingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<HorometerReadingResponseDTO> register(@RequestBody @Valid HorometerReadingRequestDTO dto) {
        HorometerReadingResponseDTO response = horometerReadingService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/equipment/{equipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<List<HorometerReadingResponseDTO>> findByEquipmentId(@PathVariable Long equipmentId) {
        List<HorometerReadingResponseDTO> response = horometerReadingService.findByEquipmentId(equipmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/equipment/{equipmentId}/period")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','OPERATOR')")
    public ResponseEntity<List<HorometerReadingResponseDTO>> findByEquipmentIdAndPeriod(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
            ){
        List<HorometerReadingResponseDTO> response = horometerReadingService.findByEquipmentAndPeriod(
                equipmentId, startDate, endDate
        );
        return ResponseEntity.ok(response);
    }
}
