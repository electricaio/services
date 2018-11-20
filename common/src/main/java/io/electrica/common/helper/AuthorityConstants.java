package io.electrica.common.helper;

import com.google.common.collect.Sets;

import java.util.Set;

public interface AuthorityConstants {

    String CREATE_SCOPE = "c";
    String READ_SCOPE = "r";
    String UPDATE_SCOPE = "u";
    String DELETE_SCOPE = "d";
    String DO_SCOPE = "do";
    String SDK_SCOPE = "sdk";

    String USER_SERVICE_RESOURCE_ID = "u";
    String CONNECTOR_HUB_SERVICE_RESOURCE_ID = "ch";
    String INVOKER_SERVICE_RESOURCE_ID = "i";
    String CONNECTOR_SERVICE_RESOURCE_ID = "c";
    String WEBHOOK_SERVICE_RESOURCE_ID = "w";

    String ACCESS_KEY_CLIENT_ID = "accessKey";
    String FRONTEND_CLIENT_ID = "frontend";

    Set<String> ACCESS_KEY_CLIENT_SCOPES = Sets.newHashSet(READ_SCOPE, SDK_SCOPE);
    Set<String> ACCESS_KEY_CLIENT_RESOURCE_IDS = Sets.newHashSet(
            USER_SERVICE_RESOURCE_ID,
            CONNECTOR_HUB_SERVICE_RESOURCE_ID,
            INVOKER_SERVICE_RESOURCE_ID,
            CONNECTOR_SERVICE_RESOURCE_ID
    );

}
