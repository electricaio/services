package io.electrica.common.helper;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ERNUtils {

    private static final String PROTOCOL = "ern://connector:";

    private ERNUtils() {
    }

    /**
     * Given the Connector's name, resource and version it,
     * builds up an ERN of ern://connector:<name>:<resource>:<version> format.
     * <p>
     * If resource is not provided, the ERN format follows this format:
     * ern://connector:<name>:<version>
     * <p>
     * In case any of the provided strings consists of multiple words, they are split by underscore.
     * <p>
     * Examples:
     * <p>
     * - ern://connector:hackerrank:applicants:1.0
     * - ern://connector:mysql:5.6
     * - ern://connector:greenhouse:1.0
     */
    public static String createERN(String name, Optional<String> resource, String version) {
        final String ernUrl = Stream
                .of(name, resource.orElse(""), version)
                // remove empty strings - case when resource is not provided
                .filter(s -> !s.isEmpty())
                .map(ERNUtils::escapeInvalidChars)
                .collect(Collectors.joining(StringUtils.COLON))
                .toLowerCase();

        return PROTOCOL + ernUrl;
    }

    public static String createERN(String name, String version) {
        return createERN(name, Optional.empty(), version);
    }

    /**
     * Given the string, it escapes multiple
     * - whitespaces,
     * - dots,
     * - colons
     * with an underscore.
     */
    protected static String escapeInvalidChars(String s) {
        return s.replaceAll("[\\s.:]+", StringUtils.UNDERSCORE);
    }
}
