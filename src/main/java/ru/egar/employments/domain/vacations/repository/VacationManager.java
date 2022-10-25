package ru.egar.employments.domain.vacations.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;

import java.util.List;

/**
 *  получение информации по отпускам сотрудника из микросервиса vacations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VacationManager {

    private final VacationClient vacationClient;

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
        List<VacationPeriodDto> validVacationsList;
        validVacationsList = vacationClient.getVacations(egarId, profileListId);
        if (validVacationsList != null && !validVacationsList.isEmpty()) {
            validVacationsList = validVacationsList.stream()
                    .filter(v -> v.getStatusType().equals(vacationStatus))
                    .toList();
        } else {
            log.info("Vacations not found");
        }
        return validVacationsList;
    }
}
