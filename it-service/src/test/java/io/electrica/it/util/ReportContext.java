package io.electrica.it.util;

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

    private ReportContext() {
    }

    public static ReportContext getInstance() {
        return instance;
    }

}

