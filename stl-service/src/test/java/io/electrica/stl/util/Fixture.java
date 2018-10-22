package io.electrica.stl.util;

import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STLType;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import io.electrica.stl.repository.AuthorizationTypeRepository;
import io.electrica.stl.repository.STLTypeRepository;
import io.electrica.stl.rest.dto.CreateSTLDto;

public interface Fixture {

    default CreateSTLDto createHackerRankSTLDto(Long typeId, Long authTypeId) {
        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName("HackerRank");
        dto.setResource("Applications");
        dto.setNamespace("com.hackerrank");
        dto.setTypeId(typeId);
        dto.setAuthorizationTypeId(authTypeId);
        dto.setVersion("1.0");
        return dto;
    }

    default CreateSTLDto createGreenhouseSTLDto(Long typeId, Long authTypeId) {
        final CreateSTLDto dto = new CreateSTLDto();
        dto.setName("Greenhouse");
        dto.setNamespace("com.greenhouse");
        dto.setTypeId(typeId);
        dto.setAuthorizationTypeId(authTypeId);
        dto.setVersion("1.1");
        return dto;
    }


    default AuthorizationType findAuthorizationType(AuthorizationTypeName name) {
        return getAuthorizationTypeRepository().findAll()
                .stream()
                .filter(at -> at.getName().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("Authorization type with passed name does not exist.")
                );
    }

    default STLType findSTLType(String name) {
        return getSTLTypeRepository().findAll()
                .stream()
                .filter(st -> st.getName().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException("STL type with passed name does not exist.")
                );
    }

    STLTypeRepository getSTLTypeRepository();

    AuthorizationTypeRepository getAuthorizationTypeRepository();
}
