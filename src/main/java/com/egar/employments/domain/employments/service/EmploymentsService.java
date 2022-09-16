package com.egar.employments.domain.employments.service;

import com.egar.employments.domain.employments.EmploymentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.egartech.sdk.api.TaskClient;
import ru.egartech.sdk.dto.task.TaskDto;
import ru.egartech.sdk.dto.task.customfield.field.label.LabelOptionDto;
import ru.egartech.sdk.dto.task.customfield.field.label.LabelsFieldDto;
import ru.egartech.sdk.dto.task.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sdk.dto.task.customfield.field.text.TextFieldDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploymentsService {

    private final TaskClient taskClient;

//    @Value("${lists.developers}")
//    private Integer devlistId;

//    @Value("${fields.egar_id}")
//    private String egarIdFieldId;

//    @Value("${fields.employments}")
//    private String employmentsFieldId;

    @Value("${fields.start_date}")
    private String startDateFieldId;

    @Value("${fields.projects}")
    private String projectsFieldId;

    /**
     * метод получения информации о занятости(название проекта, дата начала) по одному id таски
     * @param taskId - id таски где находится информация по занятости
     * @return объект занятости(название проекта, дата начала)
     */
    private EmploymentsDto getTaskById(String taskId) {
        TaskDto taskDto = taskClient.getTaskById(taskId, false);
        List<LabelOptionDto> labelOptionDtoList = taskDto.customField(projectsFieldId, LabelsFieldDto.class).getValue();
        String projectName = labelOptionDtoList.get(0).getLabel();
        String startEmploymentDate = taskDto.customField(startDateFieldId, TextFieldDto.class).getValue();
        return new EmploymentsDto(projectName, startEmploymentDate);
    }

    /**
     * метод получения списка объектов занятости по списку id тасок
     * @param taskIds
     * @return список объектов занятости(название проекта, дата начала)
     */
    public List<EmploymentsDto> getEmployments(List<String> taskIds) {
        List<EmploymentsDto> employments = taskIds.stream().map(this::getTaskById).collect(Collectors.toList());
        return employments;
    }
}
