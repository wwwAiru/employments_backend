package ru.egar.employments.domain.vacations.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.egar.employments.AbstractSpringBootTest;
import ru.egar.employments.domain.vacations.dto.VacationPeriodDto;
import ru.egar.employments.domain.vacations.repository.VacationManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class VacationServiceTest extends AbstractSpringBootTest {

    @MockBean
    private VacationManager vacationManager;

    @Autowired
    private VacationService vacationService;

    @Test
    void getVacationDatesNotEmpty() {
        List<VacationPeriodDto> vacationPeriodDtos = new ArrayList<>();
        VacationPeriodDto vacPeriod = new VacationPeriodDto(1667350800000L, 1668474000000L, "done");
        vacationPeriodDtos.add(vacPeriod);
        given(vacationManager.getVacations("username", "180311895")).willReturn(vacationPeriodDtos);
        assertThat(vacationService.getVacationDates("username", "180311895")).isNotEmpty();
    }

    @Test
    void getVacationDatesIsEmpty() {
        List<VacationPeriodDto> vacationPeriodDtos = new ArrayList<>();
        given(vacationManager.getVacations("asergeevich", "180311895")).willReturn(vacationPeriodDtos);
        assertThat(vacationService.getVacationDates("asergeevich", "180311895")).isEmpty();
    }
}