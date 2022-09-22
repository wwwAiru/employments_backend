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
import org.springframework.web.util.UriComponentsBuilder;

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

    @Value("${vacations.status}")
    private String vacationStatus;

    /**
     * метод получает даты всех отпусков сотрудника
     * @param egarId - id сотрудника
     * @param profileListId - id списка в котором находится сотрудник(тестировщик, разработчик, аналитик)
     * @return - List<VacationPeriodDto> - список периодов отпусков
     */
    @SneakyThrows
    public List<VacationPeriodDto> getVacations(String egarId, String profileListId) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("egar_id", "{egarId}")
                .queryParam("profile_list_id", "{profileListId}")
                .encode()
                .toUriString();
        Map<String, String> params = new HashMap<>();
        params.put("egarId", egarId);
        params.put("profileListId", profileListId);
        ResponseEntity<String> response = restTemplate.getForEntity(urlTemplate, String.class, params);
        List<VacationPeriodDto> vacationPeriodDtos = objectMapper.readValue(response.getBody(), new TypeReference<>(){});
        List<VacationPeriodDto> validVacationsList = vacationPeriodDtos.stream()
                .filter(v -> v.getStatus().equals(vacationStatus)).toList();
        return validVacationsList;
    }
}
