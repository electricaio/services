package io.electrica.it.context;

import io.electrica.it.auth.TokenDetails;
import io.electrica.it.model.Organization;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ContextHolder {

    private final AtomicReference<TokenDetails> tokenDetails = new AtomicReference<>();
    private final List<Organization> organizations = new ArrayList<>();

    public void clear() {
        tokenDetails.set(null);
    }

    public void addOrganizationToContext(OrganizationDto organizationDto) {
        Organization org = new Organization();
        org.setOrganizationDto(organizationDto);
        org.setUserMap(new HashMap<>());
        organizations.add(org);
    }

    public void addUserToOrganization(String orgName, UserDto userDto) {
        Optional<Organization> org = organizations.stream()
                .filter(o -> o.getOrganizationDto().getName().equalsIgnoreCase(orgName))
                .findFirst();
        if (org.isPresent()) {
            org.get().getUserMap().put(userDto.getFirstName(), userDto);
        }
    }

    public Organization getOrganizationByName(String orgName) {
        Optional<Organization> org = organizations.stream()
                .filter(o -> o.getOrganizationDto().getName().equalsIgnoreCase(orgName))
                .findFirst();
        if (org.isPresent()) {
            return org.get();
        } else {
            throw new RuntimeException("Organization " + orgName + " does not exist in context");
        }
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setTokenDetails(TokenDetails td) {
        tokenDetails.set(td);
    }

    public String getAccessToken() {
        return tokenDetails.get() == null ? null : tokenDetails.get().getAccessToken();
    }
}
