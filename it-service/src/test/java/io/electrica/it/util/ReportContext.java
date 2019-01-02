package io.electrica.it.util;

import io.electrica.sdk.java.core.Electrica;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportContext {

    private static final ReportContext instance = new ReportContext();
    private String accessKey;
    private String invokerServiceUrl;
    private String channelName;
    private Boolean publishReport;
    private Electrica electricaInstance;

    private ReportContext() {
    }

    public static ReportContext getInstance() {
        return instance;
    }

}

