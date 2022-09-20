package com.egar.employments.domain.employments.service;

import com.egar.employments.domain.employments.dto.EmploymentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class EmploymentService {

    private final TaskClient taskClient;

    @Value("${fields.start_date}")
    private String startDateFieldId;

    @Value("${fields.projects}")
    private String projectsFieldId;

    /**
     * метод получения информации о занятости(название проекта, дата начала) по одному id таски
     * @param taskId - id таски где находится информация по занятости
     * @return объект занятости(название проекта, дата начала)
     */
    private Optional<EmploymentsDto> getTaskById(String taskId) {
        TaskDto taskDto = taskClient.getTaskById(taskId, false);
        List<LabelOptionDto> labelOptionDtoList = taskDto.customField(projectsFieldId, LabelsFieldDto.class).getValue();
        String projectName = labelOptionDtoList.get(0).getLabel();
        String startEmploymentDate = taskDto.customField(startDateFieldId, TextFieldDto.class).getValue();
        if (projectName!= null & startEmploymentDate != null) {
            return Optional.of(new EmploymentsDto(projectName, startEmploymentDate));
        }
        return Optional.empty();
    }

    /**
     * метод получения списка объектов занятости по списку id тасок
     * @param taskIds
     * @return список объектов занятости(название проекта, дата начала)
     */
    public List<EmploymentsDto> getEmployments(List<String> taskIds) {
        List<EmploymentsDto> employments = taskIds.stream()
                .map(this::getTaskById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return employments;
    }
}
