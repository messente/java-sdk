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
package com.messente.sdk;

import com.messente.sdk.options.MessenteOptions;
import com.messente.sdk.enums.Country;
import com.messente.sdk.enums.ApiMethod;
import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.response.MessenteResponse;
import com.messente.sdk.exception.MessenteException;
import com.messente.sdk.response.MessenteDeliveryStatus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class is the heart & brain of this SDK. Provides flexible ways to use
 * all Messente API methods (messaging, pricing, credits and DLR).
 *
 * @author Lennar Kallas
 */
public class Messente {

    private String username;
    private String password;
    private String server;
    private String backupServer;

    private Properties properties;

    /**
     * List of keys that must be present in passed in properties file.
     */
    private final List<String> CONFIG_KEYS = Arrays.asList(
            "api.username",
            "api.password",
            "main.url",
            "backup.url");

    /**
     * Constructs Messente object with values from external properties file.
     *
     * @param configPath Path to the configuration file as string.
     * @throws com.messente.sdk.exception.MessenteException when configuration
     * file path is null or an empty string.
     */
    public Messente(String configPath) throws MessenteException {
        loadConfigValues(configPath);
    }

    /**
     * Constructs Messente object with specified API credentials and default
     * server URLs.
     *
     * @param username Messente API username,
     * @param password Messente API password.
     */
    public Messente(String username, String password) {
        this(username, password, "api2.messente.com", "api3.messente.com");
    }

    /**
     * Constructs Messente API object with specified requisites.
     *
     * @param username Messente's API username.
     * @param password Messente's API password.
     * @param server Messente main API server URL.
     * @param backupServer Messente backup API server URL.
     */
    public Messente(
            String username, String password, String server, String backupServer) {

        this.username = username;
        this.password = password;
        this.server = server;
        this.backupServer = backupServer;
    }

    /**
     * Enumeration of response types for Messente pricing API.
     */
    public enum ResponseFormat {
        JSON("json"),
        XML("xml");

        private final String format;

        private ResponseFormat(final String format) {
            this.format = format;
        }

        @Override
        public String toString() {
            return format;
        }
    }

    /**
     * Validates the configuration file and loads values to corresponding
     * variables.
     *
     * @param configPath Path to the configuration file as string.
     * @throws MessenteException when the specified path is not
     * accessible/readable.
     */
    private void loadConfigValues(String configPath) throws MessenteException {

        if (configPath != null && !configPath.trim().isEmpty()) {

            try {
                properties = new Properties();
                properties.load(new FileInputStream(configPath));

                validateConfigFile(properties);
                setValues(properties);
            } catch (FileNotFoundException ex) {
                throw new MessenteException(ex.getMessage() == null
                        ? "Specified configuration file path invalid: "
                        + configPath : ex.getMessage());
            } catch (IOException | NoSuchFieldError ex) {
                throw new MessenteException(ex.getMessage());
            }

        } else {
            throw new MessenteException("Configuration file path not specified");
        }

    }

    /**
     * Validates the configuration file used for Messente's messaging API
     * connection.
     */
    private void validateConfigFile(Properties props) throws MessenteException {

        // Throw exception if input argument is not set
        if (props == null) {
            throw new MessenteException("Properties are not set!");
        }

        validationHelper(props.stringPropertyNames(), CONFIG_KEYS);

        // Collection for unconfigured key values
        List<String> unconfiguredValues = null;

        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            if (value == null || value.trim().isEmpty()) {

                if (unconfiguredValues == null) {
                    unconfiguredValues = new ArrayList<>();
                }
                unconfiguredValues.add(key);
            }
        }

        // Throw exception if there were any unconfigured property keys
        if (unconfiguredValues != null) {

            String a = "";
            for (String s : unconfiguredValues) {
                a = unconfiguredValues.indexOf(s) == (unconfiguredValues.size() - 1)
                        ? a + s : a + s + ", ";
            }
            throw new MessenteException("Unconfigured value for: [" + a + "]");
        }
    }

    /**
     * Validates properties file keys.
     *
     * @param a Collection to check.
     * @param b Collection with required keys.
     * @throws MessenteException when there are missing required property keys.
     */
    private void validationHelper(
            Collection<String> a, Collection<String> b) throws MessenteException {

        // Collection of unconfigured keys
        List<String> missing = null;

        for (String key : b) {
            if (!a.contains(key)) {
                if (missing == null) {
                    missing = new ArrayList<>();
                }
                missing.add(key);
            }
        }

        // Throw exception if there were any unconfigured property keys
        if (missing != null) {

            String f = "";
            for (String s : missing) {
                f = missing.indexOf(s) == (missing.size() - 1)
                        ? f + s : f + s + ", ";
            }
            throw new MessenteException("Unconfigured key(s): [" + f + "]");
        }
    }

    /**
     * Sets the configured values to be used for Messente's messaging API
     * connection.
     *
     * @param props Properties containing Messente API user credentials and
     * server URL's.
     */
    private void setValues(Properties props) {

        setUsername(props.getProperty("api.username").trim());
        setPassword(props.getProperty("api.password").trim());
        setServer(props.getProperty("main.url").trim());
        setBackupServer(props.getProperty("backup.url").trim());
    }

    /**
     * @return the Messente API username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username Set Messente's API username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the Messente API password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password Messente API password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Messente's API server URL as string without protocol.
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server Main Messente API server without protocol
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return Messente's API backup server URL as string without protocol.
     */
    public String getBackupServer() {
        return backupServer;
    }

    /**
     * @param backupServer Main Messente API backup server without protocol
     */
    public void setBackupServer(String backupServer) {
        this.backupServer = backupServer;
    }

    /**
     * Sends SMS with the specified sender ID to specified recipient with
     * selected options.
     *
     * @param from Sender ID.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @param options Message options.
     * @return response from the API server.
     * @throws com.messente.sdk.exception.MessenteException
     */
    public MessenteResponse sendSMS(String from, String to, String text,
            MessenteOptions options) throws MessenteException {

        return _sendSMS(from, to, text, options);
    }

    public MessenteResponse sendSMS(
            String from, String to, String text) throws MessenteException {

        return _sendSMS(from, to, text, new MessenteOptions());
    }

    public MessenteResponse sendSMS(String to, String text) throws MessenteException {
        return _sendSMS(null, to, text, new MessenteOptions());
    }

    /**
     * Prepares phone number for API call. Removes all non-digit characters and
     * prepends '+' character.
     *
     * @param number phone number.
     * @return phone number in correct format.
     */
    private String prepareNumber(String number) {
        number = number.replaceAll("\\D+", "");
        return "+" + number;
    }

    /**
     * Creates a string with API user credentials that is usable in HTTP
     * request.
     *
     * @param username Messente API username.
     * @param password Messente API password.
     * @return
     */
    private String prepareCredentialsAsRequestParams(
            String user, String pass) throws MessenteException {

        try {
            StringBuilder b = new StringBuilder();

            b.append("username")
                    .append('=')
                    .append(URLEncoder.encode(user, "UTF-8"))
                    .append('&')
                    .append("password")
                    .append('=')
                    .append(URLEncoder.encode(pass, "UTF-8"));

            return b.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new MessenteException("Failed to urlencode API credential(s)!");
        }
    }

    private MessenteResponse _sendSMS(
            String from,
            String recipient,
            String text,
            MessenteOptions options) throws MessenteException {

        StringBuilder postData = new StringBuilder();

        appendRequestParameters(postData, options.getOptions(), true);
        if (from != null && !from.trim().isEmpty()) {
            appendRequestParameter(
                    postData,
                    "from",
                    from,
                    options.getCharset());
        }

        appendRequestParameter(postData, "text", text, options.getCharset()); // Add SMS 
        appendRequestParameter(postData, "to", prepareNumber(recipient), "UTF-8"); // Add recipient

        return sendRequest(options.getProtocol(), options.getHttpMethod(),
                ApiMethod.SEND_SMS, postData.toString());
    }

    /**
     * Dispatch method for making HTTP requests. Retry call to backup server if
     * main server failed.
     *
     * @param protocol HTTP protocol being used.
     * @param httpMethod HTTP GET or POST.
     * @param apiMethod Messente API method.
     * @param postData Data to post.
     * @return
     * @throws MessenteException
     */
    private MessenteResponse sendRequest(HttpProtocol protocol, String httpMethod,
            ApiMethod apiMethod, String postData) throws MessenteException {

        boolean retry = false;
        MessenteResponse response = null;

        URL url = getURL(protocol, getServer(), apiMethod, postData);

        response = makeHttpRequest(url, httpMethod);

        retry = !response.isSuccess()
                && (response.getResponseMessage().equals(MessenteResponse.SERVER_FAILURE)
                || response.getHttpResponseCode() != 200);

        // Retry with backup server
        if (retry && getBackupServer() != null) {
            url = getURL(protocol, getBackupServer(), apiMethod, postData);
            response = makeHttpRequest(url, httpMethod);
        }

        return response;
    }

    /**
     *
     * @param url target URL.
     * @param httpMethod HTTP POST or GET.
     * @return
     * @throws MessenteException
     */
    private MessenteResponse makeHttpRequest(URL url, String httpMethod)
            throws MessenteException {

        if (url == null) {
            throw new MessenteException("URL not provided for HTTP request!");
        }

        String postData = url.getQuery();
        HttpURLConnection conn = null;

        int responseCode = 0;
        String resp = null;

        try {

            if (url.getProtocol().equals("https")) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setRequestMethod(httpMethod);
            conn.setRequestProperty("User-Agent", "MessenteJ");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Write POST data
            if (!httpMethod.equalsIgnoreCase("GET") && postData != null) {

                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(postData);
                wr.flush();
                wr.close();
            }

            responseCode = conn.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String input;
            StringBuilder response = new StringBuilder();

            while ((input = in.readLine()) != null) {
                response.append(input);
            }
            in.close();
            resp = response.toString();
        } catch (IOException ex) {
            throw new MessenteException("Unable to read server response! "
                    + ex.getMessage() != null ? ex.getMessage() : "");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return new MessenteResponse(resp, responseCode);
    }

    /**
     *
     * @param builder
     * @param param
     * @param value
     * @param urlencode
     * @throws UnsupportedEncodingException
     */
    private void appendRequestParameter(
            StringBuilder builder,
            String param,
            String value,
            String encoding) throws MessenteException {

        if (builder == null) {
            throw new MessenteException("Cannot append request parameter '"
                    + param + "' - StringBuilder is null!");
        }

        if (builder.length() != 0) {
            builder.append('&');
        }

        try {
            builder
                    .append(param)
                    .append('=')
                    .append(encoding != null
                            ? URLEncoder.encode(value, encoding)
                            : URLEncoder.encode(value, "UTF-8"));

        } catch (UnsupportedEncodingException ex) {
            throw new MessenteException("'" + value + "' can't be encoded to "
                    + encoding != null ? encoding : "UTF-8");
        }

    }

    /**
     *
     * @param builder
     * @param params
     * @param urlencode
     * @throws UnsupportedEncodingException
     */
    private void appendRequestParameters(
            StringBuilder builder,
            Map<String, String> params,
            boolean urlencode) throws MessenteException {

        if (builder == null) {
            throw new MessenteException("Cannot append request parameters "
                    + "- StringBuilder is null!");
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            if (builder.length() != 0) {
                builder.append('&');
            }

            try {
                builder
                        .append(key)
                        .append('=')
                        .append(urlencode ? URLEncoder.encode(value, "UTF-8") : value);

            } catch (UnsupportedEncodingException ex) {
                throw new MessenteException("'" + value + "' can't be encoded to UTF-8");
            }
        }
    }

    public MessenteDeliveryStatus getDeliveryStatus(String msgid) throws MessenteException {
        return getDeliveryStatus(msgid, null);
    }

    /**
     * Gets SMS delivery status.
     *
     * @param msgid Unique message ID.
     * @param options
     * @return
     * @throws MessenteException
     */
    public MessenteDeliveryStatus getDeliveryStatus(String msgid, MessenteOptions options)
            throws MessenteException {

        if (msgid == null || msgid.trim().isEmpty()) {
            throw new MessenteException("Cannot check message delivery status "
                    + "- message ID not set!");
        }

        StringBuilder postData = new StringBuilder();
        appendRequestParameter(postData, "sms_unique_id", msgid, "UTF-8");

        // Set default options if none are set
        if (options == null) {
            options = new MessenteOptions();
        }

        MessenteResponse response = sendRequest(
                options.getProtocol(),
                options.getHttpMethod(),
                ApiMethod.GET_DLR_RESPONSE, postData.toString());

        return new MessenteDeliveryStatus(
                response.getResponse(),
                response.getHttpResponseCode());
    }

    public MessenteResponse getPriceList(Country country) throws MessenteException {

        return getPriceList(country, null, null);
    }

    public MessenteResponse getPriceList(Country country, ResponseFormat format)
            throws MessenteException {

        return getPriceList(country, format, null);
    }

    public MessenteResponse getPriceList(Country country, MessenteOptions options)
            throws MessenteException {

        return getPriceList(country, null, options);
    }

    public MessenteResponse getPriceList(Country country, ResponseFormat format,
            MessenteOptions options) throws MessenteException {

        if (country == null) {
            throw new MessenteException("Country code not provided(null)!");
        }

        StringBuilder postData = new StringBuilder();

        appendRequestParameter(postData, "country", country.toString(), "UTF-8");
        if (format != null) {
            appendRequestParameter(postData, "format", format.toString(), "UTF-8");
        }

        // Set default options if none are set
        if (options == null) {
            options = new MessenteOptions();
        }

        return sendRequest(options.getProtocol(), options.getHttpMethod(),
                ApiMethod.PRICES, postData.toString());
    }

    /**
     * Gets Messente account balance.
     *
     * @return Messente account balance as string.
     * @throws MessenteException
     */
    public MessenteResponse getBalance() throws MessenteException {
        return getBalance(null);
    }

    public MessenteResponse getBalance(MessenteOptions options) throws MessenteException {

        String credentials = prepareCredentialsAsRequestParams(getUsername(), getPassword());

        if (options == null) {
            options = new MessenteOptions();
        }

        return sendRequest(options.getProtocol(), options.getHttpMethod(),
                ApiMethod.GET_BALANCE, credentials);
    }

    public URL getMessagingURL(String from, String to, String text) throws MessenteException {

        return getMessagingURL(from, to, text, null);
    }

    public URL getMessagingURL(String to, String text) throws MessenteException {

        return getMessagingURL(null, to, text, null);
    }

    public URL getMessagingURL(String from, String to, String text, MessenteOptions options) throws MessenteException {

        if (to == null || to.trim().isEmpty()) {
            throw new MessenteException("Can't build URL : recipient is not specified!");
        }

        if (text == null || text.trim().isEmpty()) {
            throw new MessenteException("Can't build URL : SMS text is not specified!");
        }

        if (options == null) {
            options = new MessenteOptions();
        }

        StringBuilder postData = new StringBuilder();

        if (from != null && !from.trim().isEmpty()) {
            appendRequestParameter(postData, "from", from, options.getCharset());
        }

        appendRequestParameter(postData, "to", prepareNumber(to), "UTF-8");
        appendRequestParameter(postData, "text", text, options.getCharset());

        appendRequestParameters(postData, options.getOptions(), true);

        return getURL(
                options.getProtocol(),
                getServer(),
                ApiMethod.SEND_SMS,
                postData.toString());

    }

    public URL getPricingURL(ResponseFormat format, Country country) throws MessenteException {

        return getPricingURL(format, country, null);
    }

    public URL getPricingURL(ResponseFormat format, Country country,
            MessenteOptions options) throws MessenteException {

        if (country == null) {
            throw new MessenteException("Country code not provided(null)!");
        }

        if (options == null) {
            options = new MessenteOptions();
        }

        StringBuilder postData = new StringBuilder();

        appendRequestParameter(postData, "country", country.toString(), "UTF-8");

        if (format != null) {
            appendRequestParameter(postData, "format", format.toString(), "UTF-8");
        }

        return getURL(options.getProtocol(), getServer(), ApiMethod.PRICES, postData.toString());
    }

    /**
     * Gets Messente DLR API URL.
     *
     * @param msgid unique message ID.
     * @return
     * @throws MessenteException
     */
    public URL getDlrURL(String msgid) throws MessenteException {

        return getDlrURL(msgid, null);
    }

    public URL getDlrURL(String msgid, MessenteOptions options) throws MessenteException {

        if (msgid == null || msgid.trim().isEmpty()) {
            throw new MessenteException("Message ID not specified for DLR!");
        }

        if (options == null) {
            options = new MessenteOptions();
        }

        String postData = "sms_unique_id=" + msgid;
        return getURL(options.getProtocol(), getServer(), ApiMethod.GET_DLR_RESPONSE, postData);
    }

    public URL getCreditsURL() throws MessenteException {
        return getCreditsURL(null);
    }

    public URL getCreditsURL(MessenteOptions options) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        return getURL(options.getProtocol(), getServer(), ApiMethod.GET_BALANCE, null);
    }

    private URL getURL(HttpProtocol protocol, String serverUrl,
            ApiMethod apimethod, String postData) throws MessenteException {

        URL url = null;
        StringBuilder params = new StringBuilder();

        try {
            // Build URL and add request params if there are any
            String urlStr
                    = new URL(protocol.toString(), serverUrl, apimethod.toString())
                    .toString()
                    + "?" + prepareCredentialsAsRequestParams(getUsername(), getPassword())
                    + (postData != null ? "&" + postData : "");

            url = new URL(urlStr);

        } catch (MalformedURLException ex) {
            throw new MessenteException("Building URL failed "
                    + ex.getMessage() != null ? ex.getMessage() : "");
        }
        return url;
    }

    /**
     * Gets the current external IP of the client.
     *
     * @return current external IP address.
     * @throws com.messente.sdk.exception.MessenteException when the IP
     * retrieval failed.
     */
    public String getMyIP() throws MessenteException {

        MessenteResponse response;
        try {
            response = makeHttpRequest(new URL("http://bot.whatismyipaddress.com/"), "GET");
        } catch (MalformedURLException ex) {
            throw new MessenteException("Retrieving IP failed: "
                    + ex.getMessage() != null ? ex.getMessage() : "");
        }
        if (response.getHttpResponseCode() != 200) {
            return null;
        }

        return response.getResponse();
    }

    @Override
    public String toString() {
        return ""
                + "MESSENTE API username: " + getUsername() + "\n"
                + "MESSENTE API password: " + getPassword() + "\n"
                + "MESSENTE API main server: " + getServer() + "\n"
                + "MESSENTE API backup server: " + getBackupServer();
    }
}
