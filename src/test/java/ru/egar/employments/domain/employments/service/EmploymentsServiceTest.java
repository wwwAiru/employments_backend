package ru.egar.employments.domain.employments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ResourceUtils;
import ru.egar.employments.AbstractSpringBootTest;
import ru.egar.employments.model.EmploymentDto;
import ru.egar.employments.service.EmploymentsService;
import ru.egartech.sdk.api.TaskClient;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.label.LabelsFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.text.TextFieldDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EmploymentsServiceTest extends AbstractSpringBootTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmploymentsService employmentsService;

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
        TaskDto taskDto = objectMapper.readValue(
                ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX.concat("employment/valid_employment.json"))
                , TaskDto.class);
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
        assertThat(employmentsService.getEmploymentsByIds(listTaskIds)).isEqualTo(employmentDtos);
        assertThat(employmentsService.getEmploymentsByIds(listTaskIds)).isNotEmpty();
    }

    @Test
    public void getEmploymentsByTaskIdIsEmpty() throws IOException {
        List<String> listTaskIds = new ArrayList<>();
        listTaskIds.add("2z4kcma");
        TaskDto taskDto = objectMapper.readValue(
                ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX.concat("employment/not_valid_employment.json"))
                , TaskDto.class);
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
        assertThat(employmentsService.getEmploymentsByIds(listTaskIds)).isEmpty();
    }

}