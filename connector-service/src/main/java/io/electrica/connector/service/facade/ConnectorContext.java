package io.electrica.connector.service.facade;

import io.electrica.integration.spi.ServiceFacadeConfigurer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;

@Getter
@AllArgsConstructor
class ConnectorContext {

    private final OkHttpClient httpClient;

    @AllArgsConstructor
    static class Builder implements ServiceFacadeConfigurer {

        private final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        @Override
        public OkHttpClient.Builder httpClientBuilder() {
            return httpClientBuilder;
        }

        ConnectorContext build() {
            return new ConnectorContext(httpClientBuilder.build());
        }
    }
}
