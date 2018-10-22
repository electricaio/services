package io.electrica.stl.repository;

import io.electrica.common.helper.ERNUtils;
import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static io.electrica.stl.model.enums.AuthorizationTypeName.BASIC_AUTHORIZATION;

public class STLRepositoryTest extends AbstractDatabaseTest {

    /**
     * Tests a case when resource is not provided and adding
     * 2 STLs with same name should raise constraint exception on ERN,
     * since they would end up being same.
     * */
    @Test(expected = DataIntegrityViolationException.class)
    public void testSaveSTLWithoutResourceResultingInSameERN() {

        final STLType type = findSTLType("Foundation");

        final String name = "MySQL";
        final String namespace = "com.mysql";
        final String version = "5.6";
        final String ern = ERNUtils.createERN(name, version);

        final STL first = new STL();
        first.setType(type);
        first.setName(name);
        first.setVersion(version);
        first.setNamespace(namespace);
        first.setErn(ern);
        first.setAuthorizationType(
                findAuthorizationType(BASIC_AUTHORIZATION)
        );
        stlRepository.saveAndFlush(first);

        final STL second = new STL();
        second.setType(type);
        second.setName(name);
        second.setVersion(version);
        second.setNamespace(namespace);
        second.setErn(ern);
        second.setAuthorizationType(
                findAuthorizationType(BASIC_AUTHORIZATION)
        );
        stlRepository.saveAndFlush(second);
    }
}
