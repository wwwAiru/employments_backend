package ru.egar.employments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EmploymentsApplication.class, ObjectMapper.class})
@ActiveProfiles("test")
public abstract class AbstractSpringBootTest {
}
