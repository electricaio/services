package io.electrica.it.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportContext {

    private static final ReportContext instance = new ReportContext();
    private String accessKey;
    private String invokerServiceUrl;
    private Boolean publishReport;
    private String slackConnectionName;
    private String channelName;

    private ReportContext() {
    }

    public static ReportContext getInstance() {
        return instance;
    }

}

