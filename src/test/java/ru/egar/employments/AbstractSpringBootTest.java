package ru.egar.employments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = {EmploymentsApplication.class, ObjectMapper.class})
@ActiveProfiles("test")
public abstract class AbstractSpringBootTest {
}
