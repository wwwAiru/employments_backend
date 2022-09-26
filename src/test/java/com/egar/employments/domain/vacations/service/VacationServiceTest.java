package com.egar.employments.domain.vacations.service;

import com.egar.employments.AbstractSpringBootTest;
import com.egar.employments.domain.vacations.dto.VacationPeriodDto;
import com.egar.employments.domain.vacations.repository.VacationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class VacationServiceTest extends AbstractSpringBootTest {

    @MockBean
    private VacationRepository vacationRepository;

    @Autowired
    private VacationService vacationService;

    @Test
    void getVacationDates_is_not_empty() {
        List<VacationPeriodDto> vacationPeriodDtos = new ArrayList<>();
        VacationPeriodDto vacPeriod = new VacationPeriodDto(1667350800000L,1668474000000L,"done");
        vacationPeriodDtos.add(vacPeriod);
        given(vacationRepository.getVacations("username", "180311895")).willReturn(vacationPeriodDtos);
        assertThat(vacationService.getVacationDates("username", "180311895")).isNotEmpty();
    }

    @Test
    void getVacationDates_is_empty() {
        List<VacationPeriodDto> vacationPeriodDtos = new ArrayList<>();
        given(vacationRepository.getVacations("asergeevich", "180311895")).willReturn(vacationPeriodDtos);
        assertThat(vacationService.getVacationDates("asergeevich", "180311895")).isEmpty();
    }
}