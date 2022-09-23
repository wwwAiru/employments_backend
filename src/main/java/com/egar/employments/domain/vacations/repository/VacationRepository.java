package com.egar.employments.domain.vacations.repository;

import com.egar.employments.domain.vacations.dto.VacationPeriodDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class VacationRepository {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    @Value("${vacations.url}")
    private String url;

    @Value("${vacations.status_type}")
    private String vacationStatus;

    /**
     * метод получает даты всех отпусков сотрудника
     * @param egarId - id сотрудника
     * @param profileListId - id списка в котором находится сотрудник(тестировщик, разработчик, аналитик)
     * @return - List<VacationPeriodDto> - список периодов отпусков
     */
    @SneakyThrows
    public List<VacationPeriodDto> getVacations(String egarId, String profileListId) {
        Map<String, String> params = new HashMap<>();
        params.put("egarId", egarId);
        params.put("profileListId", profileListId);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, params);
        List<VacationPeriodDto> vacationPeriodDtos = objectMapper.readValue(response.getBody(), new TypeReference<>(){});
        List<VacationPeriodDto> validVacationsList = vacationPeriodDtos.stream()
                .filter(v -> v.getStatusType().equals(vacationStatus)).toList();
        return validVacationsList;
    }
}
