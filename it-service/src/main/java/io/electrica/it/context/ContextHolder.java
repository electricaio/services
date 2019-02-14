package io.electrica.it.context;

import io.electrica.it.auth.TokenDetails;
import io.electrica.it.auth.TokenManager;
import io.electrica.user.dto.OrganizationDto;
import io.electrica.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

@Component
public class ContextHolder {

    private final TokenManager tokenManager;
    private final String adminEmail;
    private final String adminPassword;

    private final ThreadLocal<TokenDetails> context = new ThreadLocal<>();
    private final List<OrganizationDto> organizations = new ArrayList<>();
    private final List<UserDto> users = new ArrayList<>();
    private final Map<String, TokenDetails> tokenStore = new HashMap<>();

    @Inject
    public ContextHolder(
            TokenManager tokenManager,
            @Value("${it-service.oauth2.user}") String adminEmail,
            @Value("${it-service.oauth2.password}") String adminPassword
    ) {
        this.tokenManager = tokenManager;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    public List<OrganizationDto> getOrganizations() {
        return organizations;
    }

    public OrganizationDto getOrganizationByName(String orgName) {
        return getOrganizations().stream()
                .filter(o -> Objects.equals(o.getName(), orgName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Organization not found in context: " + orgName));
    }

    public void addOrganizationToContext(OrganizationDto organizationDto) {
        organizations.add(organizationDto);
    }


    public List<UserDto> getUsers() {
        return users;
    }

    public void addUserToContext(UserDto userDto) {
        users.add(userDto);
    }


    public TokenDetails getToken() {
        return context.get();
    }

    public void setToken(TokenDetails td) {
        context.set(td);
    }

    public void setTokenForUser(String email) {
        TokenDetails token = tokenStore.computeIfAbsent(email, ignored -> {
            UserDto user = users.stream()
                    .filter(u -> Objects.equals(u.getEmail(), email))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found in context: " + email));
            return tokenManager.getTokenDetailsForUser(user.getEmail(), user.getFirstName());
        });
        setToken(token);
    }

    public void setTokenForAdmin() {
        TokenDetails token = tokenStore.computeIfAbsent(adminEmail, ignored ->
                tokenManager.getTokenDetailsForUser(adminEmail, adminPassword)
        );
        setToken(token);
    }
}
