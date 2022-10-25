package ru.egar.employments.domain.vacations.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;
import ru.egar.employments.error.exception.VacationsCantReceivedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class VacationClient {

    @Autowired
    @Qualifier("cuerpRestTemplate")
    private RestTemplate restTemplate;

    @Value("${vacations.url}")
    private String url;

    public List<VacationPeriodDto> getVacations(String egarId, String profileListId) {
        Map<String, String> params = new HashMap<>();
        List<VacationPeriodDto> vacationPeriods;
        params.put("egarId", egarId);
        params.put("profileListId", profileListId);
        ResponseEntity<VacationPeriodDto[]> vacationPeriodResponse = restTemplate.getForEntity(url, VacationPeriodDto[].class, params);
        if (vacationPeriodResponse.getStatusCode() != HttpStatus.OK) {
            log.warn("Bad response from vacation service");
            throw new VacationsCantReceivedException();
        } else {
            vacationPeriods = List.of(Objects.requireNonNull(vacationPeriodResponse.getBody()));
        }
        return vacationPeriods;
    }
}
