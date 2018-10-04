package io.electrica.services.stl.service.memory;

import io.electrica.services.stl.model.STL;
import io.electrica.services.stl.model.STLType;
import io.electrica.services.stl.service.STLService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InMemorySTLService extends AbstractInMemoryService<STL> implements STLService {

    @PostConstruct
    public void data(){

        final STLType type = new STLType();
        type.setId(1L);
        type.setName("Foundation");

        final STL stl = new STL();
        stl.setName("Hackerrank");
        stl.setErn("test");
        stl.setNamespace("test");
        stl.setVersion("0.0.1");
        stl.setType(type);

        save(stl);
    }
}
