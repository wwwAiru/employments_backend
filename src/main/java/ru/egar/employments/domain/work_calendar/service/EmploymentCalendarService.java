package ru.egar.employments.domain.work_calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egar.employments.domain.vacations.service.VacationService;
import ru.egar.employments.domain.work_calendar.entity.DayType;
import ru.egar.employments.domain.work_calendar.entity.Employment;
import ru.egar.employments.domain.work_calendar.entity.Project;
import ru.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
import ru.egar.employments.domain.work_calendar.repository.EmploymentDayRepository;
import ru.egar.employments.domain.work_calendar.repository.ProjectRepository;
import ru.egar.employments.domain.work_calendar.repository.WeekendAndShortDayRepository;
import ru.egar.employments.model.EmploymentCalendarDto;

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

    private final WeekendAndShortDayRepository weekendAndShortDayRepository;

    private final EmploymentDayRepository employmentDayRepository;

    private final VacationService vacationService;

    private final ProjectRepository projectRepository;

    /**
     * @param projectName - имя проекта для которого нужно вернуть календарь занятости
     * @param beginDate - дата выхода на проект.
     * @param egarId - id сотрудника
     * @param profileListId - id списка в котором находится сотрудник(списки делятся на: разработчик, аналитик, тестировщик)
     * egarId и profileListId нужны для получения дат(дней) отпусков сотрудника из микросервиса vacations.
     * @return - EmploymentCalendarDto - содержит название проекта и Map<String, HoursDto>,
     * где String - число месяца(прим. "1" - январь, "12" - декабрь), HoursDto - число рабочих часов в месяце
     * и число учтённых на проетке часов.
     */
    public ru.egar.employments.model.EmploymentCalendarDto getEmploymentCalendar(String projectName,
                                                                                 String beginDate,
                                                                                 String egarId,
                                                                                 String profileListId) {
        // получаем сет дней отпусков
        Set<LocalDate> vacationDates = vacationService.getVacationDates(egarId, profileListId);
        // дата первого дня текущего года
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        // дата последнего дня текущего года
        LocalDate lastDayOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        // получаем дату выхода на проект в миллисекундах и преобразуем в LocalDate
        LocalDate employmentStartDate = LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(beginDate)), ZoneId.of("UTC"));
        int startMonth;
        /* если дата выхода на проект раньше даты начала текущего года,
            то месяц для создания рабочего календаря устанавливается первый(январь),
            иначе начальный месяц устанавливается - месяц выхода на проект,
            дополнительно устанавливается конкретная дата выхода на проект - startDate
        */
        if (employmentStartDate.isBefore(startDate)) {
            startMonth = 1;
        } else {
            startMonth = employmentStartDate.getMonthValue();
            startDate = employmentStartDate;
        }
        // получаем из базы все выходные, праздники, сокращённые дни за период текущего года
        List<WeekendAndShortDays> weekendAndShortDays = weekendAndShortDayRepository.findWeekendAndShortDays(startDate, lastDayOfYear);
        // получаем из б.д. List<EmploymentDay> список учтённых часов на проетке по egarId и названию проета
        List<Employment> registeredHoursByYear = findRegisteredHoursByYear(egarId, projectName);

        Set<LocalDate> weekendAndHolidayOfYear = getDaysByType(DayType.WEEKEND, weekendAndShortDays);
        Set<LocalDate> shortDaysOfYear = getDaysByType(DayType.SHORTDAY, weekendAndShortDays);
        /* преобразуем список список учтённых часов на проетке в Map<LocalDate, Double>,
         ключ - дата, значение - учтённые часы(double)
         */
        Map<LocalDate, Double> employmentDaysMap = registeredHoursByYear.stream()
                .collect(Collectors.toMap(Employment::getDate, Employment::getRegisteredHours));
        /* создал Map<Integer, HoursDto> workCalendar, Integer - месяц, HoursDto - число рабочих
        и учтённых на проекте часов
         */
        Map<String, ru.egar.employments.model.HoursDto> workCalendar = new HashMap<>();
        // итерируя по месяцам года, заполняется workCalendar используя приватные методы этого сервиса
        for ( ; startMonth<=12; startMonth++) {
            ru.egar.employments.model.HoursDto hoursDto = new ru.egar.employments.model.HoursDto(
                    getWorkHoursByMonth(startMonth, startDate, weekendAndHolidayOfYear, shortDaysOfYear, vacationDates),
                    getRegisteredHoursByMonth(startMonth, employmentDaysMap)
                    );
            if (hoursDto.getWorkHours() != 0) {
                workCalendar.put(String.valueOf(startMonth), hoursDto);
            }
            startDate = null; // дата выхода на проект обнуляется после первой итерации, так как больше не нужна
        }
        return new EmploymentCalendarDto(projectName, workCalendar);
    }

    /**
     * медот принимает
     * @param month - месяц(целое число от 1 до 12)
     * @param date - дата начала (если есть)
     * @param weekendAndHolidayOfYear - сет выходных и праздничных дней,
     * @param shortDaysOfYear - сет сокращённых дней,
     * @param vacationDates - сет дней отпуска.
     * @return - количество рабочих часов в месяц
     */
    private Integer getWorkHoursByMonth(int month, LocalDate date,
                                        Set<LocalDate> weekendAndHolidayOfYear,
                                        Set<LocalDate> shortDaysOfYear,
                                        Set<LocalDate> vacationDates) {
        AtomicInteger workHours = new AtomicInteger(0);
        /* если сет отпусков не пуст, то добавить его к выходным и праздникам
            и исключить из сета сохращённых дней
        */
        if (!vacationDates.isEmpty()) {
            weekendAndHolidayOfYear.addAll(vacationDates);
            shortDaysOfYear.removeAll(vacationDates);
        }
        // если отсутствует дата с которой нужно считать, то установить мервое число месяца
        if (date == null) {
            date = LocalDate.now().withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        }
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
        // дата первого дня месяца(переданного в параметр) текущего года
        LocalDate date = LocalDate.now().withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
        // дата последнего дня месяца(переданного в параметр) текущего года
        LocalDate endDate = LocalDate.now().withMonth(month).with(TemporalAdjusters.lastDayOfMonth());
        Double registeredHours = 0.0;
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
     * @param egarId - id сотрудника
     * @param projectName - название проекта для коготорого нужно выгрузить календарь учтённых часов
     * @return - List<EmploymentDay>. EmploymentDay - содержит имя проекта(String), дату(LocalDate),
     * количество учтённых часов(int)
     */
    private List<Employment> findRegisteredHoursByYear(String egarId, String projectName) {
        // дата первого дня текущего года
        LocalDate firstDayOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        // дата последнего дня текущего года
        LocalDate lastDayOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        Project name = projectRepository.findByProjectName(projectName);
        return employmentDayRepository.findEmployment(name, egarId, firstDayOfYear, lastDayOfYear);
    }

    /**
     * @param dayType - Enum - тип дня(WEEKEND, HOLIDAY, SHORTDAY)
     * @param weekendAndShortDaysOfYear - список выходных, праздничных, сокращённых дней
     * @return Set<LocalDate> в зависимости от типа дня.
     */
    private Set<LocalDate> getDaysByType(Enum<DayType> dayType,
                                         List<WeekendAndShortDays> weekendAndShortDaysOfYear) {
        Set<LocalDate> weekendAndHolidayOfYear = new HashSet<>();
        Set<LocalDate> shortDaysOfYear = new HashSet<>();
        //заполняем два множества дат по типу дня 1 - выходной и праздник, 2 - сокращённый день
        weekendAndShortDaysOfYear.forEach(day -> {
            if (day.getDayType() == DayType.WEEKEND | day.getDayType() == DayType.HOLIDAY) {
                weekendAndHolidayOfYear.add(day.getDate());
            }
            else if (day.getDayType() == DayType.SHORTDAY){
                shortDaysOfYear.add(day.getDate());
                }
            });
        if (dayType == DayType.HOLIDAY | dayType == DayType.WEEKEND) {
            return weekendAndHolidayOfYear;
        } else {
            return shortDaysOfYear;
        }
    }

}
