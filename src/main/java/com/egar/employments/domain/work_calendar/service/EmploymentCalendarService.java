package com.egar.employments.domain.work_calendar.service;

import com.egar.employments.domain.vacations.service.VacationService;
import com.egar.employments.domain.work_calendar.dto.EmploymentCalendarDto;
import com.egar.employments.domain.work_calendar.dto.HoursDto;
import com.egar.employments.domain.work_calendar.entity.DayType;
import com.egar.employments.domain.work_calendar.entity.EmploymentDay;
import com.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
import com.egar.employments.domain.work_calendar.repository.EmploymentDayRepository;
import com.egar.employments.domain.work_calendar.repository.WorkDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploymentCalendarService {

    private final int workDayHours = 8;

    private final int shortDayHours = 7;

    private final WorkDayRepository workDayRepository;

    private final EmploymentDayRepository employmentDayRepository;

    private final VacationService vacationService;

    /**
     * @param projectName - имя проекта для которого нужно вернуть календарь занятости
     * при поиске в б.д. игнорируется регистр символов
     * @return - EmploymentCalendarDto - содержит название проекта и Map<Integer, HoursDto>,
     * где Integer - число месяца(прим. 1 - январь, 12 - декабрь), HoursDto - число рабочих часов в месяце
     * и число учтённых на проетке часов.
     */
    public EmploymentCalendarDto getEmploymentCalendar(String projectName,
                                                       String beginDate,
                                                       String egarId,
                                                       String profileListId) {
        Set<LocalDate> vacationDates = vacationService.getVacationDates(egarId, profileListId);

        // дата первого дня текущего года
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        // дата последнего дня текущего года
        LocalDate lastDayOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());

        // получаем дату выхода на проект в миллисекундах и преобразуем в LocalDate
        LocalDate localDate = LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(beginDate)), ZoneId.of("UTC"));

        int startMonth;
        /* если дата выхода на проект раньше даты начала текущего года,
            то месяц для создания рабочего календаря устанавливается первый,
            иначе начальный месяц устанавливается - месяц выхода на проект,
            дополнительно устанавливается конкретная дата выхода на проект startDate
        */
        if (localDate.isBefore(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))) {
            startMonth = 1;
        } else {
            startMonth = localDate.getMonthValue();
            startDate = localDate;
        }

        // получаем из базы все записи за период текущего года
        List<WeekendAndShortDays> shortDaysByDateBetween = workDayRepository.findWeekendAndShortDays(startDate, lastDayOfYear);
        // получаем из б.д. List<EmploymentDay> список учтённых часов на проетке по названию проета
        List<EmploymentDay> registeredHoursByYear = findRegisteredHoursByYear(projectName);
        /* преобразуем список список учтённых часов на проетке в Map<LocalDate, Double>,
         ключ - дата, значение число учтённых часов
         */
        Map<LocalDate, Double> employmentDaysMap = registeredHoursByYear.stream()
                .collect(Collectors.toMap(EmploymentDay::getDate, EmploymentDay::getRegisteredHours));
        /* создал Map<Integer, HoursDto> workCalendar, Integer - месяц, HoursDto - число рабочих
        и учтённых на проекте часов
         */
        Map<Integer, HoursDto> workCalendar = new HashMap<>();
        // итерируя по месяцам года, заполняется workCalendar используя приватные методы этого сервиса
        for ( ; startMonth<=12; startMonth++) {
            HoursDto hoursDto = new HoursDto(getWorkHoursByMonth(
                    startMonth,
                    startDate,
                    shortDaysByDateBetween,
                    vacationDates),
                    getRegisteredHoursByMonth(startMonth, employmentDaysMap)
            );
            if (hoursDto.getWorkHours() != 0) {
                workCalendar.put(startMonth, hoursDto);
            }
            startDate = null; // дата выхода на проект обнуляется после первой итерации, так как больше не нужна
        }
        return new EmploymentCalendarDto(projectName, workCalendar);
    }

    /**
     * медот принимает
     * @param month - месяц(целое число от 1 до 12)
     * @param weekendAndShortDaysOfYear - список
     * @return - количество рабочих часов в месяц
     */
    private Integer getWorkHoursByMonth(int month, LocalDate date,
                                        List<WeekendAndShortDays> weekendAndShortDaysOfYear,
                                        Set<LocalDate> vacationDates) {

        AtomicInteger workHours = new AtomicInteger(0);
        Set<LocalDate> weekendAndHolidayOfYear = new HashSet<>();
        Set<LocalDate> shortDaysOfYear = new HashSet<>();

        //заполняем два множества дат по типу дня 1 - выходной и праздник, 2 - сокращённый день
        weekendAndShortDaysOfYear.forEach(day -> {
            if (day.getDayType() == DayType.WEEKEND | day.getDayType() == DayType.HOLIDAY) {
                weekendAndHolidayOfYear.add(day.getDate());}
            else if (day.getDayType() == DayType.SHORTDAY){
                shortDaysOfYear.add(day.getDate());
            }
        });

        // если сет отпусков не пуст, то добавить его к выходным и праздникам
        if (!vacationDates.isEmpty()) {
            weekendAndHolidayOfYear.addAll(vacationDates);
        }

        if (date == null) {
            date = LocalDate.now().withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        }
        // дата первого дня месяца(переданного в параметр) текущего года
        // дата последнего дня месяца(переданного в параметр) текущего года
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        // создаётся список дней месяца monthPeriod
        List<LocalDate> monthPeriod = new ArrayList<>();
        while (!date.isAfter(endDate)) {
            monthPeriod.add(date);
            date = date.plusDays(1);
        }

        // суммируются часы обычных рабочих дней и сокращенных рабочих дней
        monthPeriod.forEach(day -> {
            if (!weekendAndHolidayOfYear.contains(day) & !shortDaysOfYear.contains(day)) {
                workHours.set(workHours.get() + workDayHours);
            }
            else if (shortDaysOfYear.contains(day)){
                workHours.set(workHours.get() + shortDayHours);
            }
        });
        return workHours.get();
    }

    /**
     * @param month - месяц(int число от 1 до 12) за который выгружаются данные по учтённым часам
     * @param employmentDaysMap - Map<LocalDate, Double> в которой ключ - дата, значение - количество учтённых часов
     * @return - возвращает количество учтённых часов(Double) на проекте за месяц(переданный в параметр)
     */
    private Double getRegisteredHoursByMonth(int month,
                                             Map<LocalDate,
                                             Double> employmentDaysMap) {
        Double registeredHours = 0.0;
        // дата первого дня месяца(переданного в параметр) текущего года
        LocalDate date = LocalDate.now().withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        // дата последнего дня месяца(переданного в параметр) текущего года
        LocalDate endDate = LocalDate.now().withMonth(month).with(TemporalAdjusters.lastDayOfMonth());

        // в цикле суммируются учтённые часы на проекте за месяц(переданный в параметр)
        while (!date.isAfter(endDate)) {
            Double hoursPerDay = employmentDaysMap.get(date);
            if (hoursPerDay != null) {
                registeredHours = registeredHours + hoursPerDay; //суммируются учтённые часы за месяц
            }
            date = date.plusDays(1);
        }
        return registeredHours;
    }

    /**
     * метод выгружает учтённые часы на проекте, на весь текущий год, по дням
     * @param projectName - имя проекта для коготорого нужно выгрузить календарь учтённых часов
     * @return - List<EmploymentDay>.
     * EmploymentDay - содержит имя проекта(String), дату(LocalDate),
     * количество учтённых часов(int)
     */
    private List<EmploymentDay> findRegisteredHoursByYear(String projectName) {
        // дата первого дня текущего года
        LocalDate firstDayOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        // дата последнего дня текущего года
        LocalDate lastDayOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        return employmentDayRepository.findEmploymentDay(projectName, firstDayOfYear, lastDayOfYear);
    }
}
