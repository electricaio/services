package io.electrica.stl.rest;

import io.electrica.STLServiceApplication;
import io.electrica.stl.dto.TestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

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
        ResponseEntity<TestDto> user = stlRestClient.createTest(new TestDto());
        Assert.notNull(user.getBody());
    }

}
