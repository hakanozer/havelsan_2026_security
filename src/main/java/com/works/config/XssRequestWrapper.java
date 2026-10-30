package com.works.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private byte[] sanitizedBody;

    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        byte[] raw = request.getInputStream().readAllBytes();
        String body = new String(raw);
        this.sanitizedBody = sanitize(body).getBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bis = new ByteArrayInputStream(sanitizedBody);
        return new ServletInputStream() {
            @Override public int read() { return bis.read(); }
            @Override public boolean isFinished() { return bis.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener listener) {}
        };
    }

    @Override
    public String getParameter(String name) {
        String val = super.getParameter(name);
        return val != null ? sanitize(val) : null;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = sanitize(values[i]);
        }
        return cleaned;
    }

    @Override
    public String getHeader(String name) {
        String val = super.getHeader(name);
        return val != null ? sanitize(val) : null;
    }

    private String sanitize(String input) {
        return Jsoup.clean(input, Safelist.none());
    }
}
