package io.electrica.connector.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.auto.service.AutoService;
import io.electrica.integration.spi.ConnectorExecutor;
import io.electrica.integration.spi.ConnectorExecutorFactory;
import io.electrica.integration.spi.ServiceFacade;
import io.electrica.integration.spi.ServiceFacadeConfigurer;
import io.electrica.integration.spi.context.ExecutionContext;
import io.electrica.integration.spi.exception.Exceptions;
import io.electrica.integration.spi.exception.IntegrationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class TestGoogleSearchConnectorExecutor implements ConnectorExecutor {

    static final String VALIDATION_MESSAGE = "Wrong request data";
    static final String ERN = "ern://test-google:search:1";
    static final AtomicInteger RESPONSE_CODE = new AtomicInteger();

    private final ServiceFacade serviceFacade;

    private final SearchParameters parameters;
    private final SearchPayload payload;

    private TestGoogleSearchConnectorExecutor(
            ServiceFacade serviceFacade,
            SearchParameters parameters,
            SearchPayload payload
    ) {
        this.serviceFacade = serviceFacade;
        this.parameters = parameters;
        this.payload = payload;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        String url = String.format(
                "https://www.google.ru/search?q=%s&start=%d",
                payload.getQuery(), parameters.getStart()
        );

        try {
            Response response = serviceFacade.getHttpClient()
                    .newCall(new Request.Builder()
                            .url(url)
                            .get()
                            .build()
                    ).execute();

            return new SearchResult(
                    response.code(),
                    response.body().string()
            );
        } catch (IOException e) {
            throw Exceptions.io("IO Exception", e);
        } catch (Exception e) {
            throw Exceptions.generic("Some generic exception", e);
        }
    }

    @AutoService(ConnectorExecutorFactory.class)
    public static class Factory implements ConnectorExecutorFactory {

        @Override
        public String getErn() {
            return ERN;
        }

        @Override
        public void configureServices(ServiceFacadeConfigurer configurer) {
            configurer.httpClientBuilder()
                    .addInterceptor(chain -> {
                        Response response = chain.proceed(chain.request());
                        RESPONSE_CODE.set(response.code());
                        return response;
                    });
        }

        @Override
        public ConnectorExecutor create(ExecutionContext context, ServiceFacade facade) throws IntegrationException {
            try {
                ObjectReader reader = facade.getObjectReader();
                SearchParameters parameters = reader.treeToValue(context.getParameters(), SearchParameters.class);
                SearchPayload payload = reader.treeToValue(context.getPayload(), SearchPayload.class);

                if (parameters.getStart() == null || payload.getQuery() == null) {
                    throw Exceptions.validation(VALIDATION_MESSAGE);
                }

                return new TestGoogleSearchConnectorExecutor(facade, parameters, payload);
            } catch (JsonProcessingException e) {
                throw Exceptions.deserialization("Can't deserialize google search contest", e);
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SearchParameters {
        private Integer start;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SearchPayload {
        private String query;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SearchResult {
        private Integer code;
        private String data;
    }
}
