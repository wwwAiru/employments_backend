package ru.egar.employments.domain.employments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.egar.employments.domain.work_calendar.service.EmploymentCalendarService;
import ru.egar.employments.model.EmploymentCalendarDto;
import ru.egar.employments.model.EmploymentDto;
import ru.egar.employments.service.EmploymentsService;
import ru.egartech.sdk.api.TaskClient;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.label.LabelOptionDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.label.LabelsFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.text.TextFieldDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EmploymentsServiceImpl implements EmploymentsService {

    private final TaskClient taskClient;

    private final EmploymentCalendarService employmentCalendarService;

    @Value("${fields.start_date.id}")
    private String startDateFieldId;

    @Value("${fields.end_date.id}")
    private String endDateFieldId;

    @Value("${fields.projects.id}")
    private String projectsFieldId;

    /**
     * метод получения информации о занятости(название проекта, дата начала) по одному id таски
     *
     * @param taskId - id таски где находится информация по занятости
     * @return объект занятости(название проекта, дата начала)
     */
    private Optional<EmploymentDto> getEmploymentsByTaskId(String taskId) {
        TaskDto taskDto = taskClient.getTaskById(taskId, false);
        List<LabelOptionDto> labelOptionDtoList = taskDto.customField(projectsFieldId, LabelsFieldDto.class).getValue();
        String projectName = labelOptionDtoList.get(0).getLabel();
        String startEmploymentDate = taskDto.customField(startDateFieldId, TextFieldDto.class).getValue();
        String endEmploymentDate = taskDto.customField(endDateFieldId, TextFieldDto.class).getValue();
        if (projectName != null & startEmploymentDate != null & endEmploymentDate == null) {
            return Optional.of(new EmploymentDto(projectName, startEmploymentDate));
        }
        return Optional.empty();
    }

    @Override
    public EmploymentCalendarDto getEmploymentCalendar(String projectName,
                                                       String beginDate,
                                                       String egarId,
                                                       String profileListId) {
        return employmentCalendarService.getEmploymentCalendar(projectName, beginDate, egarId, profileListId);
    }

    /**
     * метод получения списка объектов занятости по списку id тасок
     *
     * @param id - список id тасок
     * @return список объектов занятости(название проекта, дата начала)
     */
    @Override
    public List<EmploymentDto> getEmploymentsByIds(List<String> id) {
        return id.stream()
                .map(this::getEmploymentsByTaskId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
