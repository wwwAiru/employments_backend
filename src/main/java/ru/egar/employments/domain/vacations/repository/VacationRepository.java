package ru.egar.employments.domain.vacations.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  репозиторий обращается к микросервису vacations для получения информации по отпускам сотрудника.
 */
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
        List<VacationPeriodDto> validVacationsList = new ArrayList<>();
        ResponseEntity<String> response = null;
        params.put("egarId", egarId);
        params.put("profileListId", profileListId);
        try {
            response = restTemplate.getForEntity(url, String.class, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
        List<VacationPeriodDto> vacationPeriodDtos = objectMapper.readValue(response.getBody(), new TypeReference<>(){});
        validVacationsList = vacationPeriodDtos.stream()
                .filter(v -> v.getStatusType().equals(vacationStatus)).toList();
        }
        return validVacationsList;
    }
}
