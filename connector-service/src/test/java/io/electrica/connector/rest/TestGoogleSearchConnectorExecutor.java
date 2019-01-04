package io.electrica.connector.rest;

import com.google.auto.service.AutoService;
import io.electrica.connector.spi.*;
import io.electrica.connector.spi.exception.Exceptions;
import io.electrica.connector.spi.exception.IntegrationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestGoogleSearchConnectorExecutor implements ConnectorExecutor {

    static final String ERN = "ern://test-google:search:1";
    static final AtomicInteger RESPONSE_CODE = new AtomicInteger();
    static final AtomicBoolean AFTER_LOAD_METHOD = new AtomicBoolean();
    static final String SEARCH_ADD_INTERCEPTOR_PROPERTY_KEY = "search.add-interceptor";

    private final OkHttpClient httpClient;

    private final SearchParameters parameters;
    private final SearchPayload payload;

    private TestGoogleSearchConnectorExecutor(
            OkHttpClient httpClient,
            SearchParameters parameters,
            SearchPayload payload
    ) {
        this.httpClient = httpClient;
        this.parameters = parameters;
        this.payload = payload;
    }

    @Nullable
    @Override
    public Object run() throws IntegrationException {
        Integer start = Validations.requiredParametersField(parameters.getStart(), "start");
        String query = Validations.requiredPayloadField(payload.getQuery(), "query");

        String url = String.format("https://www.google.ru/search?q=%s&start=%d", query, start);

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();

            return new SearchResult(
                    response.code(),
                    response.body().string()
            );
        } catch (IOException e) {
            throw Exceptions.io("IO Exception", e);
        }
    }

    @AutoService(ConnectorExecutorFactory.class)
    public static class Factory implements ConnectorExecutorFactory {

        private OkHttpClient httpClient;

        @Override
        public String getErn() {
            return ERN;
        }

        @Override
        public void afterLoad() {
            AFTER_LOAD_METHOD.set(true);
        }

        @Override
        public void setup(ConnectorProperties properties) throws IntegrationException {
            boolean addInterceptor = properties.getBooleanRequired(SEARCH_ADD_INTERCEPTOR_PROPERTY_KEY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (addInterceptor) {
                builder.addInterceptor(chain -> {
                    Response response = chain.proceed(chain.request());
                    RESPONSE_CODE.set(response.code());
                    return response;
                });
            }
            httpClient = builder.build();
        }

        @Override
        public ConnectorExecutor create(ServiceFacade facade) throws IntegrationException {
            SearchParameters parameters = facade.readParameters(SearchParameters.class);
            SearchPayload payload = facade.readPayload(SearchPayload.class);
            return new TestGoogleSearchConnectorExecutor(httpClient, parameters, payload);
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
