package ru.egar.employments.domain.employments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.egar.employments.AbstractSpringBootTest;
import ru.egar.employments.model.EmploymentDto;
import ru.egartech.sdk.api.TaskClient;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.label.LabelsFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.text.TextFieldDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EmploymentsServiceImplTest extends AbstractSpringBootTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmploymentsServiceImpl employmentsServiceImpl;

    @MockBean
    private TaskClient taskClient;

    @Value("${fields.start_date.id}")
    private String startDateFieldId;

    @Value("${fields.projects.id}")
    private String projectsFieldId;

    @Test
    public void getEmploymentsByTaskIdNotEmpty() throws IOException {
        List<String> listTaskIds = new ArrayList<>();
        List<EmploymentDto> employmentDtos = new ArrayList<>();
        employmentDtos.add(new EmploymentDto("НРД", "1661994000000"));
        listTaskIds.add("2z4g3d7");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("employment/valid_employment.json");
        TaskDto taskDto = objectMapper.readValue(inputStream, TaskDto.class);
        when(taskClient.getTaskById("2z4g3d7", false)).thenReturn(taskDto);
        assertThat(taskDto
                .customField(projectsFieldId, LabelsFieldDto.class)
                .getValue()
                .get(0)
                .getLabel()).isEqualTo("НРД");
        assertThat(taskDto
                .customField(startDateFieldId, TextFieldDto.class)
                .getValue())
                .isEqualTo("1661994000000");
        assertThat(employmentsServiceImpl.getEmploymentsByIds(listTaskIds)).isEqualTo(employmentDtos);
        assertThat(employmentsServiceImpl.getEmploymentsByIds(listTaskIds)).isNotEmpty();
    }

    @Test
    public void getEmploymentsByTaskIdIsEmpty() throws IOException {
        List<String> listTaskIds = new ArrayList<>();
        listTaskIds.add("2z4kcma");
        List<EmploymentDto> employmentDtos = new ArrayList<>();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("employment/not_valid_employment.json");
        TaskDto taskDto = objectMapper.readValue(inputStream, TaskDto.class);
        when(taskClient.getTaskById("2z4kcma", false)).thenReturn(taskDto);
        assertThat(taskDto
                .customField(projectsFieldId, LabelsFieldDto.class)
                .getValue()
                .get(0)
                .getLabel()).isEqualTo("МКБ");
        assertThat(taskDto
                .customField(startDateFieldId, TextFieldDto.class)
                .getValue())
                .isNull();
        assertThat(employmentsServiceImpl.getEmploymentsByIds(listTaskIds)).isEmpty();
    }

}