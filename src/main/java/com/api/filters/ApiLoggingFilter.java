package com.api.filters;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.common.utils.LoggerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * RestAssured filter that logs every request/response pair to Log4j and the
 * Extent report (via LoggerUtils). Credentials are masked: sensitive headers
 * and "password"/"token"/"secret" fields in JSON bodies.
 */
public class ApiLoggingFilter implements Filter {

    private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization", "x-api-key", "cookie", "set-cookie");
    private static final Pattern SENSITIVE_BODY_FIELDS = Pattern.compile(
            "(\"(?:password|token|secret)\"\\s*:\\s*\")[^\"]*(\")", Pattern.CASE_INSENSITIVE);
    private static final int MAX_BODY_LENGTH = 5000;
    private static final String MASK = "****";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification responseSpec,
            FilterContext ctx) {
        Response response = ctx.next(request, responseSpec);

        String summary = String.format("%s %s -> %d (%d ms)", request.getMethod(), request.getURI(),
                response.statusCode(), response.time());
        if (response.statusCode() >= 400) {
            LoggerUtils.warn("API " + summary);
        } else {
            LoggerUtils.info("API " + summary);
        }

        String requestBody = bodyAsString(request.getBody());
        String requestText = "Headers:" + System.lineSeparator() + formatHeaders(request.getHeaders());
        if (requestBody != null && !requestBody.isBlank()) {
            requestText += System.lineSeparator() + System.lineSeparator() + "Body:" + System.lineSeparator()
                    + prettyMaskedBody(requestBody);
        }
        LoggerUtils.block("Request", requestText);

        // Response headers are mostly server/CDN noise, so they only go to the log at DEBUG level
        LoggerUtils.debug("Response headers:" + System.lineSeparator() + formatHeaders(response.getHeaders()));
        String responseBody = response.asString();
        LoggerUtils.block("Response body",
                responseBody == null || responseBody.isBlank() ? "(empty)" : prettyMaskedBody(responseBody));
        return response;
    }

    private static String formatHeaders(Headers headers) {
        return headers.asList().stream()
                .map(h -> h.getName() + ": "
                        + (SENSITIVE_HEADERS.contains(h.getName().toLowerCase()) ? MASK : h.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String bodyAsString(Object body) {
        if (body == null) {
            return null;
        }
        return body instanceof byte[] bytes ? new String(bytes) : String.valueOf(body);
    }

    private static String prettyMaskedBody(String body) {
        String pretty;
        try {
            pretty = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(MAPPER.readTree(body));
        } catch (Exception e) {
            pretty = body;
        }
        String masked = SENSITIVE_BODY_FIELDS.matcher(pretty).replaceAll("$1" + MASK + "$2");
        return masked.length() > MAX_BODY_LENGTH
                ? masked.substring(0, MAX_BODY_LENGTH) + System.lineSeparator() + "... (truncated)"
                : masked;
    }
}
