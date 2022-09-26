package ru.egar.employments.domain.vacations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;
import ru.egar.employments.domain.vacations.repository.VacationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * сервис для получения дней(дат) отпуска
 */
@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;

    public Set<LocalDate> getVacationDates(String egarId, String profileListId){
        List<VacationPeriodDto> vacationPeriodDtos = vacationRepository.getVacations(egarId, profileListId);
        Set<LocalDate> vacationDaysSet = new HashSet<>();
        if (!vacationPeriodDtos.isEmpty()) {
            for (VacationPeriodDto vacationPeriod : vacationPeriodDtos) {
                LocalDate startDate = unixToLocalDate(vacationPeriod.getStartDate());
                LocalDate endDate = unixToLocalDate(vacationPeriod.getEndDate());
                while (!startDate.isAfter(endDate)) {
                    vacationDaysSet.add(startDate);
                    startDate = startDate.plusDays(1);
                }
            }
        }
        return vacationDaysSet;
    }

    private LocalDate unixToLocalDate(Long date){
        return LocalDate.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"));
    }

}
