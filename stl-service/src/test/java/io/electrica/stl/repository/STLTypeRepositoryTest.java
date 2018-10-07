package io.electrica.stl.repository;

import io.electrica.STLServiceApplication;
import io.electrica.stl.TestProfileResolver;
import io.electrica.stl.model.STLType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
@SpringBootTest(classes = STLServiceApplication.class)
@Transactional
public class STLTypeRepositoryTest {

    @Inject
    private STLTypeRepository stlTypeRepository;

    @Before
    public void setup() {
        stlTypeRepository.deleteAll();
    }

    @Test
    public void test_create_stl_type_with_success() {
//        setup
        final STLType type = new STLType();
        type.setName("Foundation");

//        method
        stlTypeRepository.save(type);

//        assert
        final List<STLType> result = stlTypeRepository.findAll();

        assertEquals(1, result.size());

        final STLType actual = result.get(0);
        assertEquals("Foundation", actual.getName());
    }
}
