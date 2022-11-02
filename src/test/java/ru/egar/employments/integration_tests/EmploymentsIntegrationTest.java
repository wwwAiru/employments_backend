package ru.egar.employments.integration_tests;

import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.egar.employments.AbstractSpringBootTest;
import ru.egar.employments.integration_tests.dto.EmploymentCalendarDto;
import ru.egar.employments.integration_tests.dto.EmploymentDto;
import ru.egar.employments.integration_tests.utils.TokenUtil;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class EmploymentsIntegrationTest extends AbstractSpringBootTest{

    @Autowired
    private TestRestTemplate restTemplate;

    @ClassRule
    public static Network network = Network.newNetwork();

    @ClassRule
    public static GenericContainer vacationBackend = new GenericContainer<>("vacation_backend")
            .withEnv("CLICK_UP_API_TOKEN", System.getenv("CLICK_UP_API_TOKEN"))
            .withCreateContainerCmdModifier(cmd -> cmd.withName("vacation-backend").withHostName("vacation-backend"))
            .withNetwork(network)
            .withExposedPorts(8080);

    @ClassRule
    public static GenericContainer employmentsBackend = new GenericContainer<>("employments_backend")
            .withEnv("db_username", "postgres")
            .withEnv("db_password", "postgres")
            .withEnv("vacations.host", "vacation-backend:8080")
            .withEnv("CLICK_UP_API_TOKEN", System.getenv("CLICK_UP_API_TOKEN"))
            .withCreateContainerCmdModifier(cmd -> cmd.withName("employments-backend").withHostName("employments-backend"))
            .withNetwork(network)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/employments/?id=2z4g3d7").forStatusCode(401));

    @Test
    public void testGetEmployments() {
        String host = employmentsBackend.getHost();
        Integer port = employmentsBackend.getFirstMappedPort();
        String employmentsUrl = "http://"+ host +":"+ port +"/employments/?id=2z4g3d7";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + TokenUtil.getTokenFromKeycloak());
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<List<EmploymentDto>> employments = restTemplate.exchange(
                employmentsUrl,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(Objects.requireNonNull(employments.getBody()).get(0).getProjectName()).isEqualTo("НРД");
        assertThat(employments.getBody().get(0).getBeginDate()).isEqualTo("1664586000000");
    }

    @Test
    public void testGetEmploymentsCalendar() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer " + TokenUtil.getTokenFromKeycloak());
        HttpEntity request = new HttpEntity(headers);
        String host = employmentsBackend.getHost();
        Integer port = employmentsBackend.getFirstMappedPort();
        String employmentsCalendarUrl = "http://"+ host + ":" + port + "/employments/calendar";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(employmentsCalendarUrl)
                .queryParam("project_name", "{projectName}")
                .queryParam("begin_date", "{beginDate}")
                .queryParam("egar_id", "{egarId}")
                .queryParam("profile_list_id", "{profileListId}")
                .encode()
                .toUriString();
        Map<String, String> params = new HashMap<>();
        params.put("projectName", "НРД");
        params.put("beginDate", "1661994000000");
        params.put("egarId", "username");
        params.put("profileListId", "180311895");
        ResponseEntity<EmploymentCalendarDto> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                request,
                EmploymentCalendarDto.class,
                params
        );
        assertThat(Objects.requireNonNull(response.getBody()).getProjectName()).isEqualTo("НРД");
        assertThat(response.getBody().getWorkCalendar()).isNotEmpty();
    }

}
