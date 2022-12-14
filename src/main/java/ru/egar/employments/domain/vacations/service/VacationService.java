package ru.egar.employments.domain.vacations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;
import ru.egar.employments.domain.vacations.repository.VacationManager;
import ru.egar.employments.util.DateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * сервис для преобразования данных из микросервиса vacations в Set<LocalDate> - сет дней(дат) отпуска сотрудника
 */
@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationManager vacationManager;

    public Set<LocalDate> getVacationDates(String egarId, String profileListId) {
        List<VacationPeriodDto> vacationPeriodDtos = vacationManager.getVacations(egarId, profileListId);
        Set<LocalDate> vacationDaysSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(vacationPeriodDtos)) {
            for (VacationPeriodDto vacationPeriod : vacationPeriodDtos) {
                LocalDate startDate = DateUtil.unixToLocalDate(vacationPeriod.getStartDate());
                LocalDate endDate = DateUtil.unixToLocalDate(vacationPeriod.getEndDate());
                while (!startDate.isAfter(endDate)) {
                    vacationDaysSet.add(startDate);
                    startDate = startDate.plusDays(1);
                }
            }
        }
        return vacationDaysSet;
    }
}
