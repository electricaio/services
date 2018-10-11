package io.electrica.stl.util;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ERNUtils {

    private static final String PROTOCOL = "stl://";

    private ERNUtils() {
    }

    /**
     * Given the STL's name, resource and version it,
     * builds up an ERN of stl://<name>:<resource>:<version> format.
     * <p>
     * If resource is not provided, the ERN format follows this format:
     * stl://<name>:<version>
     * <p>
     * In case any of the provided strings consists of multiple words, they are split by underscore.
     * <p>
     * Examples:
     * <p>
     * - stl://hackerrank:applicants:1.0
     * - stl://mysql:5.6
     * - stl://greenhouse:1.0
     */
    public static String createERN(String name, Optional<String> resource, String version) {
        final String ernUrl = Stream
                .of(name, resource.orElse(""), version)
                // replace whitespaces with single underscore
                .map(s -> s.replaceAll("\\s+", StringUtil.UNDERSCORE))
                // filter out empty strings - case when resource is not provided
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(StringUtil.COLON))
                .toLowerCase();

        return PROTOCOL + ernUrl;
    }

    public static String createERN(String name, String version) {
        return createERN(name, Optional.empty(), version);
    }
}
