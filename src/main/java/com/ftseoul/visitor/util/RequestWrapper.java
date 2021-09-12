package com.ftseoul.visitor.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.IOUtils;

@EqualsAndHashCode
public class RequestWrapper {

    private HttpServletRequest request;

    private RequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

    public static RequestWrapper of(HttpServletRequest request) {
        return new RequestWrapper(request);
    }

    public static RequestWrapper of(ServletRequest request) {
        return of((HttpServletRequest) request);
    }

    public Map<String, String> headerMap() {
        Map<String, String> convertedHeaderMap = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            convertedHeaderMap.put(headerName, headerValue);
        }
        return convertedHeaderMap;
    }

    public String getBodyContents() throws IOException {
        String result = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        return result;
    }

    public Map<String, String> parameterMap() {
        Map<String, String> convertedParameterMap = new HashMap<>();
        Map<String, String[]> parameterMap = this.request.getParameterMap();

        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            StringJoiner joiner = new StringJoiner(",");
            for (String value : values) {
                joiner.add(value);
            }
            convertedParameterMap.put(key, joiner.toString());
        }
        return convertedParameterMap;
    }

    public String getRequestUri() {
        return request.getRequestURI();
    }

    public String getIPAddress() {
        return request.getRemoteAddr();
    }
}
