package ru.egar.employments.domain.employments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.egar.employments.model.EmploymentDto;
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
     * метод получения списка объектов занятости по списку id тасок
     * @param taskIds - список id тасок
     * @return список объектов занятости(название проекта, дата начала)
     */
    public List<ru.egar.employments.model.EmploymentDto> getEmployments(List<String> taskIds) {
        List<ru.egar.employments.model.EmploymentDto> employments = taskIds.stream()
                .map(this::getEmploymentsByTaskId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return employments;
    }

    /**
     * метод получения информации о занятости(название проекта, дата начала) по одному id таски
     * @param taskId - id таски где находится информация по занятости
     * @return объект занятости(название проекта, дата начала)
     */
    private Optional<ru.egar.employments.model.EmploymentDto> getEmploymentsByTaskId(String taskId) {
        TaskDto taskDto = taskClient.getTaskById(taskId, false);
        List<LabelOptionDto> labelOptionDtoList = taskDto.customField(projectsFieldId, LabelsFieldDto.class).getValue();
        String projectName = labelOptionDtoList.get(0).getLabel();
        String startEmploymentDate = taskDto.customField(startDateFieldId, TextFieldDto.class).getValue();
        if (projectName!= null & startEmploymentDate != null) {
            return Optional.of(new EmploymentDto(projectName, startEmploymentDate));
        }
        return Optional.empty();
    }
}
