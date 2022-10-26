package ru.egar.employments.domain.vacations.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;
import ru.egar.employments.error.exception.VacationsReceiveException;

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
        List<VacationPeriodDto> vacationPeriods;
        Map<String, String> params = new HashMap<>();
        params.put("egarId", egarId);
        params.put("profileListId", profileListId);
        ResponseEntity<List<VacationPeriodDto>> vacationPeriodResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}, params);
        if (vacationPeriodResponse.getStatusCode() != HttpStatus.OK) {
            log.warn("Bad response from vacation service");
            throw new VacationsReceiveException();
        } else {
            vacationPeriods = Objects.requireNonNull(vacationPeriodResponse.getBody());
        }
        return vacationPeriods;
    }
}
