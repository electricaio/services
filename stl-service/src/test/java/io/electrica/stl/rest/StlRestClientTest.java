package io.electrica.stl.rest;

import io.electrica.STLServiceApplication;
import io.electrica.stl.dto.TestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = STLServiceApplication.class,
        properties = ""
)
public class StlRestClientTest {

    @Autowired
    private StlRestClient stlRestClient;

    @Test
    public void createTest() {
        ResponseEntity<TestDto> response = stlRestClient.createTest(new TestDto());
        assertNotNull(response.getBody());
    }

}
