package io.electrica.common.helper;

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
    String WEBSOCKET_SERVICE_RESOURCE_ID = "ws";
    String INTEGRATION_TEST_SERVICE_RESOURCE_ID = "it";

    String ACCESS_KEY_CLIENT_ID = "accessKey";
    String FRONTEND_CLIENT_ID = "frontend";

    String[] FRONTEND_CLIENT_SCOPES = {CREATE_SCOPE, READ_SCOPE, UPDATE_SCOPE, DELETE_SCOPE, DO_SCOPE};
    String[] FRONTEND_CLIENT_RESOURCE_IDS = {
            USER_SERVICE_RESOURCE_ID,
            CONNECTOR_HUB_SERVICE_RESOURCE_ID,
            WEBHOOK_SERVICE_RESOURCE_ID
    };

    String[] ACCESS_KEY_CLIENT_SCOPES = {READ_SCOPE, SDK_SCOPE};
    String[] ACCESS_KEY_CLIENT_RESOURCE_IDS = {
            USER_SERVICE_RESOURCE_ID,
            CONNECTOR_HUB_SERVICE_RESOURCE_ID,
            INVOKER_SERVICE_RESOURCE_ID,
            CONNECTOR_SERVICE_RESOURCE_ID,
            WEBHOOK_SERVICE_RESOURCE_ID,
            WEBSOCKET_SERVICE_RESOURCE_ID
    };

}
