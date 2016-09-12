/*
 * Copyright 2016 Messente Communications Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.messente.sdk.options;

import com.messente.sdk.enums.Autoconvert;
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

    // SMS messaging API
    private String timeToSend;
    private String dlrUrl;
    private String charset;
    private String validity;
    private String udh;
    private String autoconvert;

    // Verification
    private String ip;
    private String browser;
    private String verifyMaxTries;
    private String verifyRetryDelay;
    private String verifyValidity;

    // General
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
        this.ip = builder.ip;
        this.browser = builder.browser;
        this.verifyMaxTries = builder.verifyMaxTries;
        this.verifyRetryDelay = builder.verifyRetryDelay;
        this.verifyValidity = builder.verifyValidity;
    }

    /**
     * Gets the HttpProtocol object that is used for making API call.
     *
     * @return HTTP or HTTPS protocol. HTTPS is the default protocol if protocol
     * is not defined.
     */
    public HttpProtocol getProtocol() {
        return httpProtocol != null ? httpProtocol : HttpProtocol.HTTPS;
    }

    /**
     * Sets the HTTP protocol used for API calls.
     *
     * @param protocol HTTP protocol (HTTP or HTTPS).
     */
    public void setProtocol(HttpProtocol protocol) {
        this.httpProtocol = protocol;
    }

    /**
     * Gets the HTTP method used for API calls.
     *
     * @return HTTP GET or POST method. POST is the default method.
     */
    public String getHttpMethod() {
        return httpMethod != null ? httpMethod.toString() : HttpMethod.POST.toString();
    }

    /**
     * Sets the HTTP (POST or GET) method used for API calls.
     *
     * @param httpMethod HTTP POST/GET method.
     */
    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Gets the time when the SMS should be sent.
     *
     * @return scheduled time for SMS sending as string, null if not set.
     */
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

    /**
     * Gets the DLR URL if set.
     *
     * @return DLR URL as string, null if not set.
     */
    public String getDlrUrl() {
        return dlrUrl;
    }

    /**
     *
     * @param dlrUrl DLR URL to set.
     */
    public void setDlrUrl(String dlrUrl) {
        this.dlrUrl = dlrUrl;
    }

    /**
     * Gets the charset of the message/sender ID if set.
     *
     * @return charset that is used in SMS text/sender ID, null if not set.
     */
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

    /**
     * Gets the validity of the SMS message.
     *
     * @return validity (in minutes) of the SMS message as string, null if not
     * set.
     */
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

    /**
     * Sets the value for how long the message is re-tried when the phone is
     * switched off (in minutes).
     *
     * @param validity SMS validity in minutes.
     */
    public void setValidity(int validity) {
        this.validity = String.valueOf(validity);
    }

    /**
     * Gets the character autoreplace opton used for SMS text.
     *
     * @return Autoconvert option, null if not set.
     */
    public String getAutoconvert() {
        return autoconvert; // != null ? autoconvert : Autoconvert.ON.toString();
    }

    /**
     * Sets SMS text character autoreplace option.
     *
     * @see
     * <a href="http://messente.com/documentation/auto-replace">http://messente.com/documentation/auto-replace</a>
     *
     * @param convert character autoreplace option.
     */
    public void setAutoconvert(Autoconvert convert) {
        this.autoconvert = convert.toString();
    }

    /**
     * Sets SMS text character autoreplace option.
     *
     * @see
     * <a href="http://messente.com/documentation/auto-replace">http://messente.com/documentation/auto-replace</a>
     * @param convert character autoreplace option. Available choices are ON,
     * FULL, OFF.
     */
    public void setAutoconvert(String convert) {
        this.autoconvert = convert;
    }

    /**
     * Gets the UDH of the SMS message.
     *
     * @return UDH as string, null if not set.
     */
    public String getUdh() {
        return udh;
    }

    /**
     * Sets the UDH for SMS message.
     *
     * @param udh UDH to set.
     */
    public void setUdh(String udh) {
        this.udh = udh;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getVerifyMaxTries() {
        return verifyMaxTries;
    }

    public void setVerifyMaxTries(String verifyMaxTries) {
        this.verifyMaxTries = verifyMaxTries;
    }

    public String getVerifyRetryDelay() {
        return verifyRetryDelay;
    }

    public void setVerifyRetryDelay(String verifyRetryDelay) {
        this.verifyRetryDelay = verifyRetryDelay;
    }

    public String getVerifyValidity() {
        return verifyValidity;
    }

    public void setVerifyValidity(String verifyValidity) {
        this.verifyValidity = verifyValidity;
    }

    /**
     * Inner static class for simple building of Messente options.
     */
    public static class Builder {

        // Verification
        private String ip;
        private String browser;
        private String verifyMaxTries;
        private String verifyRetryDelay;
        private String verifyValidity;

        // SMS messaging
        private String timeToSend;
        private String dlrUrl;
        private String charset;
        private String validity;
        private String autoconvert;
        private String udh;

        // General
        private HttpProtocol httpProtocol;
        private HttpMethod httpMethod;

        // Below: Builder methods to set property.
        /**
         * Optional. IP address of the client making verification request.
         *
         * @param ip IP address of the verification client.
         * @return this.
         */
        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        /**
         * Optional verification parameter. User Agent of the browser. For
         * example "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3)
         * AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130
         * Safari/537.36"
         *
         * @param browser User agent of the browser.
         * @return this.
         */
        public Builder browser(String browser) {
            this.browser = browser;
            return this;
        }

        /**
         * Optional. Maximum number of times the PIN code is sent in total.
         * Defaults to "2" - initial PIN code and one retry. It is discouraged
         * to set this value to "1" as only the initial PIN code is sent and
         * retry is disabled.
         *
         * @param maxTries Maximum number of times the PIN code is sent in
         * total.
         * @return this.
         */
        public Builder verifyMaxTries(String maxTries) {
            this.verifyMaxTries = maxTries;
            return this;
        }

        /**
         * Optional. For how long (in seconds) to wait for next retry, if the
         * correct PIN code has not been entered yet? Defaults to 30 seconds.
         *
         * @param delay For how long (in seconds) to wait for next verification
         * retry.
         * @return this.
         */
        public Builder verifyRetryDelay(String delay) {
            this.verifyRetryDelay = delay;
            return this;
        }

        /**
         * Optional. For how long (in seconds) is the PIN code valid. Defaults
         * to 5 minutes (300 seconds). Maximum 30 minutes (1800 seconds).
         *
         * @param validity For how long (in seconds) is the PIN code valid.
         * @return this.
         */
        public Builder verifyValidity(String validity) {
            this.verifyValidity = validity;
            return this;
        }

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
     * Gets options for verification session as map.
     *
     * @return map with verification session options.
     */
    public Map<String, String> getVerifySessionStartOptions() {

        Map<String, String> ops = new HashMap<>();

        if (ip != null && !ip.trim().isEmpty()) {
            ops.put("ip", ip);
        }

        if (browser != null && !browser.trim().isEmpty()) {
            ops.put("browser", browser);
        }

        if (verifyMaxTries != null && !verifyMaxTries.trim().isEmpty()) {
            ops.put("max_tries", verifyMaxTries);
        }

        if (verifyRetryDelay != null && !verifyRetryDelay.trim().isEmpty()) {
            ops.put("retry_delay", verifyRetryDelay);
        }

        if (verifyValidity != null && !verifyValidity.trim().isEmpty()) {
            ops.put("validity", verifyValidity);
        }

        return ops;
    }

    /**
     * Gets SMS sending options that are set as map.
     *
     * @return map with SMS sending options.
     */
    public Map<String, String> getSmsSendingOptions() {

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
     * Gets options for SMS sending as request parameters.
     *
     * @param ops Map with options.
     * @return StringBuilder with request parameters.
     * @throws MessenteException when encoding parameter value to UTF-8 fails.
     */
    public StringBuilder getOptionsAsRequestParams(Map<String, String> ops) throws MessenteException {

        StringBuilder params = new StringBuilder();

        for (Map.Entry<String, String> entry : ops.entrySet()) {
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
                + "Protocol: " + getProtocol() + "\n"
                + "IP: " + getIp() + "\n"
                + "Browser: " + getBrowser() + "\n"
                + "Verifixation max tries: " + getVerifyMaxTries() + "\n"
                + "Verification retry delay: " + getVerifyRetryDelay() + "\n"
                + "Verification validity: " + getVerifyValidity();
    }

}
