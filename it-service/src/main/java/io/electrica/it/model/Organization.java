package io.electrica.it.model;

import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Organization {

    OrganizationDto organizationDto;
    Map<String, UserDto> userMap;
}
