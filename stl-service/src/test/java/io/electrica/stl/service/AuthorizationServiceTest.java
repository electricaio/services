package io.electrica.stl.service;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.Connection;
import io.electrica.stl.model.enums.AuthorizationTypeName;
import io.electrica.stl.rest.dto.AuthorizationDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    private Connection connection;

    @Before
    public void setup() {
        connection = new Connection();
        connection.setId(1L);
    }

    @Test(expected = BadRequestServiceException.class)
    public void testCreateBasicAuthWithWrongAuthData() {
        final AuthorizationType type = new AuthorizationType(AuthorizationTypeName.BASIC_AUTHORIZATION);

        final AuthorizationDto authorizationDto = new AuthorizationDto();
        authorizationDto.setDetails("test");

        authorizationService.createBasicAuth(type, connection, authorizationDto);
    }

    @Test(expected = BadRequestServiceException.class)
    public void testCreateAwsIamAuthWithWrongAuthData() {
        final AuthorizationType type = new AuthorizationType(AuthorizationTypeName.AWS_IAM_AUTHORIZATION);

        final AuthorizationDto authorizationDto = new AuthorizationDto();
        authorizationDto.setToken("test");

        authorizationService.createBasicAuth(type, connection, authorizationDto);
    }

    @Test(expected = BadRequestServiceException.class)
    public void testCreateTokenAuthWithWrongAuthData() {
        final AuthorizationType type = new AuthorizationType(AuthorizationTypeName.TOKEN_AUTHORIZATION);

        final AuthorizationDto authorizationDto = new AuthorizationDto();
        authorizationDto.setUser("test");

        authorizationService.createBasicAuth(type, connection, authorizationDto);
    }
}
