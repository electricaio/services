package io.electrica.it.context;

import io.electrica.it.auth.TokenDetails;
import io.electrica.it.auth.TokenManager;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContextHolder {

    private static final String ADMIN_EMAIL = "admin@electrica.io";
    private static final String ADMIN_PASSWORD = "admin";

    private TokenManager tokenManager;

    public ContextHolder(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    private final ThreadLocal<TokenDetails> context = new ThreadLocal<>();
    private final List<OrganizationDto> organizations = new ArrayList<>();
    private final List<UserDto> users = new ArrayList<>();
    private final Map<String, TokenDetails> tokenStore = new HashMap<>();

    public void clear() {
        context.set(null);
    }

    public void addOrganizationToContext(OrganizationDto organizationDto) {
        organizations.add(organizationDto);
    }

    public void addUserToContext(UserDto userDto) {
        users.add(userDto);
    }

    public void setContextForUser(String email) {
        TokenDetails td = tokenStore.get(email);
        if (td == null) {
            Optional<UserDto> userDtoOptional = users.stream()
                    .filter(u -> Objects.equals(u.getEmail(), email))
                    .findFirst();
            if (userDtoOptional.isPresent()) {
                UserDto userDto = userDtoOptional.get();
                td = tokenManager.getTokenDetailsForUser(userDto.getEmail(), userDto.getFirstName());
                tokenStore.put(userDto.getEmail(), td);
            } else {
                throw new RuntimeException("User with Email '" + email + "' does not exist in context");
            }
        }
        context.set(td);
    }

    public void setContextForAdmin() {
        TokenDetails tokenDetails = tokenStore.get(ADMIN_EMAIL);
        if (tokenDetails == null) {
            tokenDetails = tokenManager.getTokenDetailsForUser(ADMIN_EMAIL, ADMIN_PASSWORD);
            tokenStore.put(ADMIN_EMAIL, tokenDetails);
        }
        context.set(tokenDetails);
    }

    public OrganizationDto getOrganizationByName(String orgName) {
        Optional<OrganizationDto> org = organizations.stream()
                .filter(o -> o.getName().equalsIgnoreCase(orgName))
                .findFirst();
        if (org.isPresent()) {
            return org.get();
        } else {
            throw new RuntimeException("Organization " + orgName + " does not exist in context");
        }
    }

    public List<OrganizationDto> getOrganizations() {
        return organizations;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setContext(TokenDetails td) {
        context.set(td);
    }

    public String getAccessToken() {
        return context.get() == null ? null : context.get().getAccessToken();
    }
}
