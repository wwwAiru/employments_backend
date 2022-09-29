package ru.egar.employments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = {EmploymentsApplication.class, ObjectMapper.class})
public abstract class AbstractSpringBootTest {
}
