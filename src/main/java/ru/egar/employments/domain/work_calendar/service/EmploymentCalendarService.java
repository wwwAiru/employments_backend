package ru.egar.employments.domain.work_calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.egar.employments.domain.vacations.service.VacationService;
import ru.egar.employments.domain.work_calendar.entity.DayType;
import ru.egar.employments.domain.work_calendar.entity.Employment;
import ru.egar.employments.domain.work_calendar.entity.Project;
import ru.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
import ru.egar.employments.domain.work_calendar.repository.EmploymentDayRepository;
import ru.egar.employments.domain.work_calendar.repository.ProjectRepository;
import ru.egar.employments.domain.work_calendar.repository.WeekendAndShortDayRepository;
import ru.egar.employments.model.EmploymentCalendarDto;
import ru.egar.employments.model.HoursDto;
import ru.egar.employments.util.DateUtil;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploymentCalendarService {

    private final int workDayHours = 8;

    private final int shortDayHours = 7;

    private final WeekendAndShortDayRepository weekendAndShortDayRepository;

    private final EmploymentDayRepository employmentDayRepository;

    private final VacationService vacationService;

    private final ProjectRepository projectRepository;

    /**
     * @param projectName   - имя проекта для которого нужно вернуть календарь занятости
     * @param beginDate     - дата выхода на проект.
     * @param egarId        - id сотрудника
     * @param profileListId - id списка в котором находится сотрудник(списки делятся на: разработчик, аналитик, тестировщик)
     *                      egarId и profileListId нужны для получения дат(дней) отпусков сотрудника из микросервиса vacations.
     * @return - EmploymentCalendarDto - содержит название проекта и Map<String, HoursDto>,
     * где String - число месяца(прим. "1" - январь, "12" - декабрь), HoursDto - число рабочих часов в месяце
     * и число учтённых на проетке часов.
     */
    public EmploymentCalendarDto getEmploymentCalendar(String projectName,
                                                       String beginDate,
                                                       String egarId,
                                                       String profileListId) {
        Set<LocalDate> vacationDates = vacationService.getVacationDates(egarId, profileListId);
        LocalDate startDate = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        LocalDate employmentStartDate = DateUtil.unixToLocalDate(Long.parseLong(beginDate));
        // получаем из базы все выходные, праздники, сокращённые дни за период текущего года
        List<WeekendAndShortDays> weekendAndShortDays = weekendAndShortDayRepository.findWeekendAndShortDays(startDate, endDate);
        // получаем из б.д. List<EmploymentDay> список учтённых часов на проетке по egarId и названию проета
        Set<LocalDate> weekendAndHoliday = getDaysByType(DayType.WEEKEND, weekendAndShortDays);
        Set<LocalDate> shortDays = getDaysByType(DayType.SHORTDAY, weekendAndShortDays);
        List<Employment> registeredHours = findRegisteredHours(egarId, projectName);
        /* преобразуем список список учтённых часов на проетке в Map<LocalDate, Double>,
         ключ - дата, значение - учтённые часы(double)
         */
        Map<LocalDate, Double> employmentDaysMap = registeredHours.stream()
                .collect(Collectors.toMap(Employment::getDate, Employment::getRegisteredHours));
        Map<String, Map<String, HoursDto>> workCalendar = new HashMap<>();
        EmploymentCalendarDto employmentCalendarDto = new EmploymentCalendarDto();
        employmentCalendarDto.setProjectName(projectName);
        Map<String, HoursDto> empCalendarByCurrentYear = getEmpCalendarByCurrentYear(
                employmentStartDate,
                weekendAndHoliday,
                shortDays,
                vacationDates,
                employmentDaysMap);
        Map<String, HoursDto> empCalendarByPreviousYear = getEmpCalendarByPreviousYear(employmentStartDate,
                weekendAndHoliday,
                shortDays,
                vacationDates,
                employmentDaysMap);
        workCalendar.put(String.valueOf(LocalDate.now().getYear()), empCalendarByCurrentYear);
        workCalendar.put(String.valueOf(LocalDate.now().minusYears(1).getYear()), empCalendarByPreviousYear);
        employmentCalendarDto.setWorkCalendar(workCalendar);
        return employmentCalendarDto;
    }

    /**
     * @param beginEmploymentDate     - дата начала работы на проекте
     * @param weekendAndHoliday       - сет выходных и праздничных дней,
     * @param shortDays               - сет сокращённых дней,
     * @param vacationDates           - сет дней отпуска
     * @param employmentDaysMap       - Map<LocalDate, Double>, ключ - дата, значение - учтённые часы(double)
     * @return                        - Map<String, HoursDto> количество рабочих и учтённых часов в месяц
     */
    private Map<String, HoursDto> getEmpCalendarByCurrentYear(LocalDate beginEmploymentDate,
                                                             Set<LocalDate> weekendAndHoliday,
                                                             Set<LocalDate> shortDays,
                                                             Set<LocalDate> vacationDates,
                                                             Map<LocalDate, Double> employmentDaysMap) {
        Map<String, HoursDto> workCalendar = new HashMap<>();
        int monthValue = LocalDate.now().getMonthValue();
        for (int i = 1; i <= monthValue; i++) {
            HoursDto hoursDto = new HoursDto(
                    getWorkHoursByMonth(i, LocalDate.now().getYear(), beginEmploymentDate, weekendAndHoliday, shortDays, vacationDates),
                    getRegisteredHoursByMonth(i, LocalDate.now().getYear(), employmentDaysMap)
            );
            if (hoursDto.getWorkHours() != 0) {
                workCalendar.put(String.valueOf(i), hoursDto);
            }
        }
        return workCalendar;
    }

    /**
     * @param beginEmploymentDate     - дата начала работы на проекте
     * @param weekendAndHoliday       - сет выходных и праздничных дней,
     * @param shortDays               - сет сокращённых дней,
     * @param vacationDates           - сет дней отпуска
     * @param employmentDaysMap       - Map<LocalDate, Double>, ключ - дата, значение - учтённые часы(double)
     * @return                        - HoursDto количество рабочих и учтённых часов
     */
    private Map<String, HoursDto> getEmpCalendarByPreviousYear(LocalDate beginEmploymentDate,
                                                              Set<LocalDate> weekendAndHoliday,
                                                              Set<LocalDate> shortDays,
                                                              Set<LocalDate> vacationDates,
                                                              Map<LocalDate, Double> employmentDaysMap) {
        Map<String, HoursDto> workCalendar = new HashMap<>();
        int monthValue = LocalDate.now().getMonthValue();
        for (int i = 12; i > monthValue; i--) {
            HoursDto hoursDto = new HoursDto(
                    getWorkHoursByMonth(i,
                            LocalDate.now().minusYears(1).getYear(),
                            beginEmploymentDate,
                            weekendAndHoliday,
                            shortDays,
                            vacationDates),
                    getRegisteredHoursByMonth(i, LocalDate.now().minusYears(1).getYear(), employmentDaysMap)
            );
            workCalendar.put(String.valueOf(i), hoursDto);
        }
        return workCalendar;
    }

    /**
     * @param month                   - месяц(целое число от 1 до 12)
     * @param year                    - год
     * @param beginEmploymentDate     - дата начала работы на проекте
     * @param weekendAndHoliday       - сет выходных и праздничных дней,
     * @param shortDays               - сет сокращённых дней,
     * @param vacationDates           - сет дней отпуска.
     * @return                        - количество рабочих часов в месяц
     */
    private Integer getWorkHoursByMonth(int month, int year, LocalDate beginEmploymentDate,
                                        Set<LocalDate> weekendAndHoliday,
                                        Set<LocalDate> shortDays,
                                        Set<LocalDate> vacationDates) {
        AtomicInteger workHours = new AtomicInteger(0);
        LocalDate startDate = LocalDate.now().withYear(year).withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        // если месяц и год соответствуют дате начала работы на проекте, то считать часы с даты начала работы
        if (beginEmploymentDate.getMonthValue() == month & beginEmploymentDate.getYear() == year) {
            startDate = beginEmploymentDate;
        }
        /* если сет отпусков не пуст, то добавить его к выходным и праздникам
           и исключить из сета сохращённых дней
        */
        if (!CollectionUtils.isEmpty(vacationDates)) {
            weekendAndHoliday.addAll(vacationDates);
            shortDays.removeAll(vacationDates);
        }
        List<LocalDate> monthPeriod = new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            monthPeriod.add(startDate);
            startDate = startDate.plusDays(1);
        }
        // суммируются часы обычных рабочих дней и сокращенных рабочих дней
        monthPeriod.forEach(day -> {
            if (!weekendAndHoliday.contains(day) & !shortDays.contains(day)) {
                workHours.set(workHours.get() + workDayHours);
            } else if (shortDays.contains(day)) {
                workHours.set(workHours.get() + shortDayHours);
            }
        });
        return workHours.get();
    }

    /**
     * @param month - месяц(int число от 1 до 12) за который выгружаются данные по учтённым часам
     * @param year - год
     * @param employmentDaysMap - Map<LocalDate, Double> в которой ключ - дата, значение - количество учтённых часов
     * @return - возвращает количество учтённых часов(Double) на проекте за месяц(переданный в параметр)
     */
    private Double getRegisteredHoursByMonth(int month, int year,
                                             Map<LocalDate,
                                             Double> employmentDaysMap) {
        // дата первого дня месяца(переданного в параметр) текущего года
        LocalDate statDate = LocalDate.now().withYear(year).withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        // дата последнего дня месяца(переданного в параметр) текущего года
        LocalDate endDate = LocalDate.now().withYear(year).withMonth(month).with(TemporalAdjusters.lastDayOfMonth());
        Double registeredHours = 0.0;
        // в цикле суммируются учтённые часы на проекте за месяц(переданный в параметр)
        while (!statDate.isAfter(endDate)) {
            Double hoursPerDay = employmentDaysMap.get(statDate);
            if (hoursPerDay != null) {
                registeredHours = registeredHours + hoursPerDay; //суммируются учтённые часы за месяц
            }
            statDate = statDate.plusDays(1);
        }
        return registeredHours;
    }

    /**
     * @param egarId - id сотрудника
     * @param projectName - название проекта для коготорого нужно выгрузить календарь учтённых часов
     * @return - List<EmploymentDay>. EmploymentDay - содержит имя проекта(String), дату(LocalDate),
     * количество учтённых часов(int)
     */
    private List<Employment> findRegisteredHours(String egarId, String projectName) {
        LocalDate startDate = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
        LocalDate endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        Project name = projectRepository.findByProjectName(projectName);
        return employmentDayRepository.findEmployment(name, egarId, startDate, endDate);
    }

    /**
     * @param dayType - Enum - тип дня(WEEKEND, HOLIDAY, SHORTDAY)
     * @param weekendAndShortDays - список выходных, праздничных, сокращённых дней
     * @return Set<LocalDate> в зависимости от типа дня.
     */
    private Set<LocalDate> getDaysByType(Enum<DayType> dayType,
                                         List<WeekendAndShortDays> weekendAndShortDays) {
        Set<LocalDate> weekendAndHoliday = new HashSet<>();
        Set<LocalDate> shortDays = new HashSet<>();
        //заполняем два множества дат по типу дня 1 - выходной и праздник, 2 - сокращённый день
        weekendAndShortDays.forEach(day -> {
            if (day.getDayType() == DayType.WEEKEND | day.getDayType() == DayType.HOLIDAY) {
                weekendAndHoliday.add(day.getDate());
            } else if (day.getDayType() == DayType.SHORTDAY) {
                shortDays.add(day.getDate());
            }
        });
        if (dayType == DayType.HOLIDAY | dayType == DayType.WEEKEND) {
            return weekendAndHoliday;
        } else {
            return shortDays;
        }
    }

}
