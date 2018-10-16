package io.electrica.stl.service.mapper;

import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

//@Mapper(componentModel = "jsr330")
@Component
public abstract class STLMapper {

//    @Inject
//    private STLTypeRepository stlTypeRepository;

    @PersistenceContext
    private EntityManager em;

    @Mapping(source = "typeId", target = "type")
    public abstract STL toEntity(CreateSTLDto dto);

    @Mapping(source = "type.id", target = "typeId")
    public abstract ReadSTLDto toDto(STL model);

//    public STLType toEntity(Long id) {
//        return Optional.of(id)
//                .flatMap(stlTypeRepository::findById)
//                .orElse(null);
//    }

    public STLType toEntity(Long id) {
        return Optional.of(id)
                .map(i -> em.getReference(STLType.class, i))
                .orElse(null);
    }
}
