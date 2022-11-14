//package ru.egar.employments.utils;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import ru.egar.employments.domain.work_calendar.entity.DayType;
//import ru.egar.employments.domain.work_calendar.entity.Employment;
//import ru.egar.employments.domain.work_calendar.entity.Project;
//import ru.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
//import ru.egar.employments.domain.work_calendar.repository.EmploymentDayRepository;
//import ru.egar.employments.domain.work_calendar.repository.ProjectRepository;
//import ru.egar.employments.domain.work_calendar.repository.WeekendAndShortDayRepository;
//
//import javax.annotation.PostConstruct;
//import java.time.LocalDate;
//import java.time.format.TextStyle;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//@Service
//public class AddDataToDBService {
//
//    @Autowired
//    private EmploymentDayRepository employmentDayRepository;
//
//    @Autowired
//    private WeekendAndShortDayRepository weekendAndShortDayRepository;
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @Autowired
//    private ProductionCalendarService productionCalendarService;
//
//    @PostConstruct
//    public void addData() throws Exception {
//        weekendAndShortDayRepository.deleteAll();
//        employmentDayRepository.deleteAll();
//        productionCalendarService.createProductionCalendar();
//        LocalDate start = LocalDate.parse("2022-01-01");
//        LocalDate end = LocalDate.parse("2022-12-31");
//        Project proj = new Project(1L, "НРД");
//        projectRepository.save(proj);
//        List<Employment> employments = new ArrayList<>();
//        while (!start.isAfter(end)) {
//            WeekendAndShortDays weekendAndShortDays = new WeekendAndShortDays(start);
//            String dayOfWeek = start.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
//            WeekendAndShortDays byDate = weekendAndShortDayRepository.findByDate(start);
//            if (byDate == null) {
//                Project project = projectRepository.getReferenceById(1L);
//                Employment employment = new Employment(project, "username");
//                employment.setDate(start);
//                employment.setRegisteredHours(8.0d);
//                employments.add(employment);
//                employmentDayRepository.save(employment);
//            } else if (byDate != null && byDate.getDayType() == DayType.SHORTDAY) {
//                Project project = projectRepository.getReferenceById(1L);
//                Employment employment = new Employment(project, "username");
//                employment.setDate(start);
//                employment.setRegisteredHours(7.0d);
//                employments.add(employment);
//                employmentDayRepository.save(employment);
//            }
//            start = start.plusDays(1);
//        }
//    }
//}
