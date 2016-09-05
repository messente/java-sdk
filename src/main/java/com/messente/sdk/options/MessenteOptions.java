/**
 * Copyright 2016 Lennar Kallas, Messente Communications Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.messente.sdk.options;

import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.enums.HttpMethod;
import com.messente.sdk.exception.MessenteException;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * SMS message options for Messente API.
 *
 * @author Lennar Kallas
 */
public class MessenteOptions {

    private String timeToSend;
    private String dlrUrl;
    private String charset;
    private String validity;
    private String udh;
    private String autoconvert;

    private HttpMethod httpMethod;
    private HttpProtocol httpProtocol;

    public MessenteOptions() {

    }

    private MessenteOptions(Builder builder) {
        this.timeToSend = builder.timeToSend;
        this.dlrUrl = builder.dlrUrl;
        this.charset = builder.charset;
        this.validity = builder.validity;
        this.autoconvert = builder.autoconvert;
        this.udh = builder.udh;
        this.httpProtocol = builder.httpProtocol;
        this.httpMethod = builder.httpMethod;
    }

    /**
     * Gets the httpProtocol that is used for making API call.
     *
     * @return
     */
    public HttpProtocol getProtocol() {
        return httpProtocol != null ? httpProtocol : HttpProtocol.HTTPS;
    }

    public void setProtocol(HttpProtocol protocol) {
        this.httpProtocol = protocol;
    }

    public String getHttpMethod() {
        return httpMethod != null ? httpMethod.toString() : HttpMethod.POST.toString();
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Enumeration of Messente character replacement options.
     */
    public enum Autoconvert {

        ON("on"),
        OFF("off"),
        FULL("full");

        private final String convert;

        private Autoconvert(final String convert) {
            this.convert = convert;
        }

        @Override
        public String toString() {
            return convert;
        }
    }

    public String getTimeToSend() {
        return timeToSend;
        //== null ? String.valueOf(Instant.now().getEpochSecond()) : timeToSend;
    }

    /**
     * Sets the time when message is sent. Useful for sending delayed messages.
     *
     * @param timeToSend Must be numeric Unix timestamp i.e. 1417190104. If the
     * time_to_send is set in past, message will be sent with no delays.
     */
    public void setTimeToSend(String timeToSend) {
        this.timeToSend = timeToSend;
    }

    public String getDlrUrl() {
        return dlrUrl;
    }

    public void setDlrUrl(String dlrUrl) {
        this.dlrUrl = dlrUrl;
    }

    public String getCharset() {
        return charset == null ? "UTF-8" : charset;
    }

    /**
     * Set the charset of the SMS message and sender ID.
     *
     * @param charset Encoding of the "text" and "from" parameter value.
     * Defaults to UTF-8
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getValidity() {
        return validity;
    }

    /**
     * Sets the value for how long the message is re-tried when the phone is
     * switched off (in minutes).
     *
     * @param validity SMS validity in minutes.
     */
    public void setValidity(String validity) {
        this.validity = validity;
    }

    public void setValidity(int validity) {
        this.validity = String.valueOf(validity);
    }

    public String getAutoconvert() {
        return autoconvert; // != null ? autoconvert : Autoconvert.ON.toString();
    }

    /**
     * Sets character autoreplace option.
     *
     * @param convert
     */
    public void setAutoconvert(Autoconvert convert) {

        this.autoconvert = convert.toString();
    }

    /**
     * Sets character autoreplace option.
     *
     * @param convert
     */
    public void setAutoconvert(String convert) {
        this.autoconvert = convert;
    }

    public String getUdh() {
        return udh;
    }

    public void setUdh(String udh) {
        this.udh = udh;
    }

    public static class Builder {

        private String timeToSend;
        private String dlrUrl;
        private String charset;
        private String validity;
        private String autoconvert;
        private String udh;

        private HttpProtocol httpProtocol;
        private HttpMethod httpMethod;

        // Builder methods to set property
        public Builder timeToSend(String when) {
            this.timeToSend = when;
            return this;
        }

        public Builder dlrUrl(String url) {
            this.dlrUrl = url;
            return this;
        }

        public Builder charset(String cSet) {
            this.charset = cSet;
            return this;
        }

        public Builder validity(String valid) {
            this.validity = valid;
            return this;
        }

        public Builder autoconvert(String convert) {
            this.autoconvert = convert;
            return this;
        }

        public Builder autoconvert(Autoconvert convert) {
            this.autoconvert = convert.toString();
            return this;
        }

        public Builder udh(String _udh) {
            this.udh = _udh;
            return this;
        }

        public Builder protocol(HttpProtocol protocol) {
            this.httpProtocol = protocol;
            return this;
        }

        public Builder httpMethod(HttpMethod method) {
            this.httpMethod = method;
            return this;
        }

        public MessenteOptions build() {
            return new MessenteOptions(this);
        }

    }

    /**
     * Gets options that are set (not NULL) as map.
     *
     * @return map with options.
     */
    public Map<String, String> getOptions() {

        Map<String, String> options = new HashMap<>();

        if (getDlrUrl() != null && !getDlrUrl().trim().isEmpty()) {
            options.put("dlr-url", getDlrUrl());
        }

        if (getValidity() != null && !getValidity().trim().isEmpty()) {
            options.put("validity", getValidity());
        }

        if (getUdh() != null && !getUdh().trim().isEmpty()) {
            options.put("udh", getUdh());
        }

        if (getTimeToSend() != null && !getTimeToSend().trim().isEmpty()) {
            options.put("time_to_send", getTimeToSend());
        }

        if (getCharset() != null && !getCharset().trim().isEmpty()) {
            options.put("charset", getCharset());
        }

        if (getAutoconvert() != null && !getAutoconvert().trim().isEmpty()) {
            options.put("autoconvert", getAutoconvert());
        }

        return options;
    }

    /**
     * Gets options as request parameters.
     *
     * @return StringBuilder with request parameters.
     * @throws MessenteException when encoding parameter value to UTF-8 fails.
     */
    public StringBuilder getOptionsAsRequestParams() throws MessenteException {

        StringBuilder params = new StringBuilder();

        for (Map.Entry<String, String> entry : getOptions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (params.length() != 0) {
                params.append('&');
            }

            try {
                params
                        .append(key)
                        .append('=')
                        .append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new MessenteException("Unable to encode options parameter "
                        + "value(s)!", ex.getCause(), true, true);
            }
        }
        return params;

    }

    @Override
    public String toString() {
        return ""
                + "Time to send: " + getTimeToSend() + "\n"
                + "DLR url: " + getDlrUrl() + "\n"
                + "Charset: " + getCharset() + "\n"
                + "Validity: " + getValidity() + "\n"
                + "Autoconvert: " + getAutoconvert() + "\n"
                + "UDH: " + getUdh() + "\n"
                + "Protocol: " + getProtocol();
    }

}
