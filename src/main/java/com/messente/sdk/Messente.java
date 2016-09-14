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
package com.messente.sdk;

import com.messente.sdk.options.MessenteOptions;
import com.messente.sdk.enums.Country;
import com.messente.sdk.enums.ApiMethod;
import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.enums.ResponseFormat;
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
 * This class is the heart &amp; brain of this SDK. Provides flexible ways to
 * use all Messente API methods (messaging, pricing, credits and DLR).
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
     * List of keys that must be present in properties file that is used for
     * creating Messente object.
     */
    private final List<String> CONFIG_KEYS = Arrays.asList(
            "messente.api.username",
            "messente.api.password",
            "messente.main.url",
            "messente.backup.url");

    /**
     * Constructs Messente object with values from external properties file.
     *
     * @param configPath Full path to the configuration file as string.
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
     * @param backupServer Messente API backup server URL.
     */
    public Messente(
            String username, String password, String server, String backupServer) {

        this.username = username;
        this.password = password;
        this.server = server;
        this.backupServer = backupServer;
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
     * Validates properties file by comparing list of mandatory keys and keys in
     * the properties file.
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

        setUsername(props.getProperty("messente.api.username").trim());
        setPassword(props.getProperty("messente.api.password").trim());
        setServer(props.getProperty("messente.main.url").trim());
        setBackupServer(props.getProperty("messente.backup.url").trim());
    }

    /**
     * Gets the configured username of Messente API.
     *
     * @return the Messente API username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets Messente API username.
     *
     * @param username Set Messente's API username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the configured password of Messente API.
     *
     * @return the Messente API password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets Messente API password.
     *
     * @param password Messente API password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the API server URL.
     *
     * @return Messente's API server URL as string (without protocol).
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the URL of Messente's API server. URL must not contain protocol, or
     * forward slashes. The correct format is for example api2.messente.com
     *
     * @param server Main Messente API server (without protocol).
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Gets the API backup server URL.
     *
     * @return Messente's API backup server URL as string (without protocol).
     */
    public String getBackupServer() {
        return backupServer;
    }

    /**
     * Sets the URL of Messente's API backup server. URL must not contain
     * protocol, or forward slashes. The correct format is for example
     * api3.messente.com
     *
     * @param backupServer Main Messente API backup server without protocol
     */
    public void setBackupServer(String backupServer) {
        this.backupServer = backupServer;
    }

    /**
     * Verifies PIN code.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public MessenteResponse verifyPin(String verificationId, String pin) throws MessenteException {
        return verifyPin(verificationId, pin, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public URL getPinVerificationURL(String verificationId, String pin) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, null, null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public String getPinVerificationUrlAsString(String verificationId, String pin) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, new MessenteOptions(), null).toString();
    }

    /**
     * Verifies PIN code.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param options Customized options to use for API call.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public MessenteResponse verifyPin(String verificationId, String pin, MessenteOptions options) throws MessenteException {
        return verifyPin(verificationId, pin, options, null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param options Customized options to use for API call.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public URL getPinVerificationURL(String verificationId, String pin, MessenteOptions options) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, options, null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param options Customized options to use for API call.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public String getPinVerificationUrlAsString(String verificationId, String pin, MessenteOptions options) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, options, null).toString();
    }

    /**
     * Verifies PIN code.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param cookie Unique cookie assigned to verification session.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public MessenteResponse verifyPin(String verificationId, String pin, String cookie) throws MessenteException {
        return verifyPin(verificationId, pin, new MessenteOptions(), cookie);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param cookie Unique cookie assigned to verification session.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID, cookie or PIN entered.
     */
    public URL getPinVerificationURL(String verificationId, String pin, String cookie) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, new MessenteOptions(), cookie);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param cookie Unique cookie assigned to verification session.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID, cookie or PIN entered.
     */
    public String getPinVerificationUrlAsString(String verificationId, String pin, String cookie) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, new MessenteOptions(), cookie).toString();
    }

    /**
     * Verifies PIN code.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param options Customized options to use for API call.
     * @param cookie Unique cookie assigned to verification session.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public MessenteResponse verifyPin(String verificationId, String pin, MessenteOptions options, String cookie) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        URL url = getPinVerificationURL(verificationId, pin, options, cookie);

        return sendRequest(url, options.getHttpMethod());
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param options Customized options to use for API call.
     * @param cookie Unique cookie assigned to verification session.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public String getPinVerificationUrlAsString(String verificationId, String pin, MessenteOptions options, String cookie) throws MessenteException {
        return getPinVerificationURL(verificationId, pin, options, cookie).toString();
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param verificationId Verification ID.
     * @param pin PIN code user entered.
     * @param options Customized options to use for API call.
     * @param cookie Unique cookie assigned to verification session.
     * @return Correctly formatted URL for PIN verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * ID or PIN entered.
     */
    public URL getPinVerificationURL(String verificationId, String pin, MessenteOptions options, String cookie) throws MessenteException {
        // Check verification ID
        if (verificationId == null || verificationId.trim().isEmpty()) {
            throw new MessenteException("Missing Verification ID!");
        }

        // Check PIN
        if (pin == null || pin.trim().isEmpty()) {
            throw new MessenteException("PIN missing!");
        }

        // Check cookie (can't be empty string)
        if (cookie != null && cookie.trim().isEmpty()) {
            throw new MessenteException("Invalid cookie");
        }

        // Set default options
        if (options == null) {
            options = new MessenteOptions();
        }

        StringBuilder postData = new StringBuilder();

        // Get pre-defined options for verification session
        Map<String, String> verifyOps = options.getPinVerifyOptions();
        // Check pre defined options map
        if (verifyOps != null && !verifyOps.isEmpty()) {
            appendRequestParameters(postData, verifyOps, true);
        }

        // Check and add cookie
        if (cookie != null && !cookie.trim().isEmpty()) {
            appendRequestParameter(postData, "cookie", cookie, "UTF-8"); // Add cookie

        }

        appendRequestParameter(postData, "pin", pin, "UTF-8"); // Add pin
        appendRequestParameter(postData, "verification_id", verificationId, "UTF-8"); // Add verification ID

        return buildURL(options.getProtocol(), ApiMethod.VERIFY_PIN, postData.toString());
    }

    /**
     * Starts the verification session.
     *
     * @param to Recipient's phone number that will receive PIN via SMS.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public MessenteResponse startVerificationSession(String to) throws MessenteException {
        return startVerificationSession(null, to, null, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public URL getStartVerificationURL(String to) throws MessenteException {
        return getStartVerificationURL(null, to, null, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public String getStartVerificationURLAsString(String to) throws MessenteException {
        return getStartVerificationURL(null, to, null, new MessenteOptions(), null).toString();
    }

    /**
     * Starts the verification session.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number that will receive PIN via SMS.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public MessenteResponse startVerificationSession(String from, String to) throws MessenteException {
        return startVerificationSession(from, to, null, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public URL getStartVerificationURL(String from, String to) throws MessenteException {
        return getStartVerificationURL(from, to, null, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public String getStartVerificationURLAsString(String from, String to) throws MessenteException {
        return getStartVerificationURL(from, to, null, new MessenteOptions(), null).toString();
    }

    /**
     * Starts the verification session.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number that will receive PIN via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public MessenteResponse startVerificationSession(String from, String to, String template) throws MessenteException {
        return startVerificationSession(from, to, template, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public URL getStartVerificationURL(String from, String to, String template) throws MessenteException {
        return getStartVerificationURL(from, to, template, new MessenteOptions(), null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public String getStartVerificationURLAsString(String from, String to, String template) throws MessenteException {
        return getStartVerificationURL(from, to, template, new MessenteOptions(), null).toString();
    }

    /**
     * Starts the verification session.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number that will receive PIN via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @param options Customized options for API call.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public MessenteResponse startVerificationSession(String from, String to, String template, MessenteOptions options) throws MessenteException {
        return startVerificationSession(from, to, template, options, null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @param options Customized options for API call.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public URL getStartVerificationURL(String from, String to, String template, MessenteOptions options) throws MessenteException {
        return getStartVerificationURL(from, to, template, options, null);
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @param options Customized options for API call.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public String getStartVerificationURLAsString(String from, String to, String template, MessenteOptions options) throws MessenteException {
        return getStartVerificationURL(from, to, template, options, null).toString();
    }

    /**
     * Starts the verification session.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number that will receive PIN via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @param options Customized options for API call.
     * @param cookie Unique cookie assigned for this session.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public MessenteResponse startVerificationSession(String from, String to, String template, MessenteOptions options, String cookie) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }
        URL url = getStartVerificationURL(from, to, template, options, cookie);

        return sendRequest(url, options.getHttpMethod());
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @param options Customized options for API call.
     * @param cookie Unique cookie assigned for this session.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public String getStartVerificationURLAsString(String from, String to, String template, MessenteOptions options, String cookie) throws MessenteException {
        return getStartVerificationURL(from, to, template, options, cookie).toString();
    }

    /**
     * Gets the URL for PIN code verification.
     *
     * @param from Sender ID that is used when PIN code is sent via SMS. Note
     * that this sender ID must be activated by Messente.
     * @param to Recipient's phone number where to send PIN code via SMS.
     * @param template Template of the SMS message. Must contain placeholder
     * %3CPIN%3E for PIN.
     * @param options Customized options for API call.
     * @param cookie Unique cookie assigned for this session.
     * @return Correctly formatted URL for verification API call.
     * @throws MessenteException when invalid(empty or null string) verification
     * recipient, cookie entered or template is missing %3CPIN%3E placeholder.
     */
    public URL getStartVerificationURL(
            String from,
            String to,
            String template,
            MessenteOptions options,
            String cookie) throws MessenteException {

        // Check SMS template for required placeholder
        if ((template != null && !template.trim().isEmpty()) && !template.contains("<PIN>")) {
            throw new MessenteException(
                    "Verification message template "
                    + "is missing '<PIN>' placeholder!");
        }

        // Check phone number
        if (!to.replaceAll("\\D+", "").matches("\\d+")) {
            throw new MessenteException("Invalid recipient's phone number!");
        }

        // Check cookie
        if (cookie != null && cookie.trim().isEmpty()) {
            throw new MessenteException("Invalid cookie");
        }

        StringBuilder postData = new StringBuilder();

        // Get pre-defined options for verification session
        Map<String, String> verifyOps = options.getVerifySessionStartOptions();

        // Check pre defined options map
        if (verifyOps != null && !verifyOps.isEmpty()) {
            appendRequestParameters(postData, verifyOps, true);
        }

        // Set 'from' parameter
        if (from != null && !from.trim().isEmpty()) {
            appendRequestParameter(
                    postData,
                    "from",
                    from,
                    options.getCharset());
        }

        // Check and add template
        if (template != null && !template.trim().isEmpty()) {
            appendRequestParameter(postData, "template", template, "UTF-8");
        }

        // Check and add cookie
        if (cookie != null && !cookie.trim().isEmpty()) {
            appendRequestParameter(postData, "cookie", cookie, "UTF-8"); // Add cookie
        }

        appendRequestParameter(postData, "to", preparePhoneNumber(to), "UTF-8"); // Add recipient

        return buildURL(options.getProtocol(), ApiMethod.VERIFY_START, postData.toString());
    }

    /**
     * Builds URL with given parameters.
     *
     * @param protocol Protocol used in URL. HTTP/HTTPS.
     * @param apiMethod Messente's API that is used for URL building.
     * @param params Correctly formatted request parameters.
     * @return Correctly formatted URL.
     * @throws MessenteException On URL building failure. Malformed request
     * parameters for example.
     */
    private URL buildURL(HttpProtocol protocol, ApiMethod apiMethod, String params) throws MessenteException {

        URL url = null;

        try {
            // Build URL and add request params if there are any
            String urlStr
                    = new URL(protocol.toString(), getServer(), apiMethod.toString())
                    .toString()
                    + "?" + prepareCredentialsAsRequestParams(getUsername(), getPassword())
                    + (params != null ? "&" + params : "");

            url = new URL(urlStr);

        } catch (MalformedURLException ex) {
            throw new MessenteException("Building URL failed "
                    + ex.getMessage() != null ? ex.getMessage() : "");
        }
        return url;
    }

    /**
     * Sends SMS with the specified sender ID to specified recipient with
     * selected options.
     *
     * @param from Registered Sender ID.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @param options Message options.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException if sending SMS failed.
     */
    public MessenteResponse sendSMS(String from, String to, String text,
            MessenteOptions options) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        URL url = getMessagingURL(from, to, text, options);

        return sendRequest(url, options.getHttpMethod());
    }

    /**
     * Sends SMS with the specified sender ID to specified recipient with
     * default options.
     *
     * @param from Registered Sender ID.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException if sending SMS failed.
     */
    public MessenteResponse sendSMS(
            String from, String to, String text) throws MessenteException {

        return sendSMS(from, to, text, new MessenteOptions());
    }

    /**
     * Sends SMS with default sender ID that is set for the specified API
     * account to specified recipient with default options.
     *
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException if sending SMS failed.
     */
    public MessenteResponse sendSMS(String to, String text) throws MessenteException {
        return sendSMS(null, to, text, new MessenteOptions());
    }

    /**
     * Prepares phone number for API call. Removes all non-digit characters and
     * adds '+' character as prefix.
     *
     * @param number phone number.
     * @return phone number in correct format for using Messente API call.
     */
    private String preparePhoneNumber(String number) {
        number = number.replaceAll("\\D+", "");
        return "+" + number;
    }

    /**
     * Creates a string with API user credentials that is usable in HTTP
     * request.
     *
     * @param username Messente API username.
     * @param password Messente API password.
     * @return Credentials as HTTP request parameters.
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

    /**
     * Dispatch method for making HTTP requests. Retry call to backup server if
     * main server failed.
     *
     * @param url URL of the request.
     * @param httpMethod HTTP POST/GET method used for request.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException if HTTP request fails.
     */
    private MessenteResponse sendRequest(URL url, String httpMethod) throws MessenteException {

        boolean retry = false;
        MessenteResponse response = null;

        response = makeHttpRequest(url, httpMethod);

        retry = !response.isSuccess()
                && (response.getResponseMessage().equals(MessenteResponse.SERVER_FAILURE)
                || response.getHttpResponseCode() != 200);

        // Retry with backup server
        if (retry && getBackupServer() != null) {

            try {
                url = new URL(url.toString().replaceFirst(server, backupServer));
                response = makeHttpRequest(url, httpMethod);
            } catch (MalformedURLException ex) {
                return response;
            }
        }
        return response;
    }

    /**
     * Takes care of making HTTP request to given URL.
     *
     * @param url target URL.
     * @param httpMethod HTTP POST or GET.
     * @return response from the API server as MessenteResponse object.
     * @throws MessenteException if HTTP request fails.
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
            conn.setRequestProperty("User-Agent", "Messente-SDK");
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
     * @param builder StringBuilder with existing parameters.
     * @param param HTTP parameter name.
     * @param value HTTP parameter value.
     * @param encoding Encoding scheme used to encode HTTP parameter.
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
     * @param builder StringBuilder with existing parameters.
     * @param params Reguest parameters as map.
     * @param urlencode sets whether parameter should be encoded in UTF-8.
     * @throws MessenteException if Stringbuilder parameter is null or encoding
     * to UTF-8 fails.
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

    /**
     * Gets the delivery status of SMS.
     *
     * @param msgid Unique message ID.
     * @return Delivery status as MessenteDeliveryStatus object.
     * @throws MessenteException if msgid is not specified or HTTP request
     * fails.
     */
    public MessenteDeliveryStatus getDeliveryStatus(String msgid) throws MessenteException {
        return getDeliveryStatus(msgid, null);
    }

    /**
     * Gets SMS delivery status.
     *
     * @param msgid Unique message ID.
     * @param options Specified options.
     * @return MessenteDeliveryStatus object with delivery status.
     * @throws MessenteException if msgid is not set.
     */
    public MessenteDeliveryStatus getDeliveryStatus(String msgid, MessenteOptions options)
            throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        URL url = getDlrURL(msgid, options);

        MessenteResponse response = sendRequest(url, options.getHttpMethod());

        return new MessenteDeliveryStatus(
                response.getRawResponse(),
                response.getHttpResponseCode());
    }

    /**
     * Gets the pricelist for given country.
     *
     * @param country Country which pricelist is requested.
     * @return MessenteResponse object with pricelist.
     * @throws MessenteException if country is not specified.
     */
    public MessenteResponse getPriceList(Country country) throws MessenteException {
        return getPriceList(country, null, new MessenteOptions());
    }

    /**
     * Gets the pricelist for given country in specified response format.
     *
     * @param country Country which pricelist is requested.
     * @param format Format of the response. JSON or XML.
     * @return MessenteResponse object with pricelist.
     * @throws MessenteException if country is not specified.
     */
    public MessenteResponse getPriceList(Country country, ResponseFormat format)
            throws MessenteException {

        return getPriceList(country, format, null);
    }

    /**
     * Gets the pricelist for given country in specified response format.
     *
     * @param country Country which pricelist is requested.
     * @param options Customized options to use for API call.
     * @return MessenteResponse object with pricelist.
     * @throws MessenteException if country is not specified.
     */
    public MessenteResponse getPriceList(Country country, MessenteOptions options)
            throws MessenteException {

        return getPriceList(country, null, options);
    }

    /**
     * Gets the pricelist for given country &amp; response format.
     *
     * @param country Country which pricelist is requested.
     * @param format Format of the response. JSON or XML.
     * @param options Customized options to use for API call.
     * @return MessenteResponse object with pricelist.
     * @throws MessenteException if country is not specified.
     */
    public MessenteResponse getPriceList(Country country, ResponseFormat format,
            MessenteOptions options) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        URL url = getPricingURL(format, country, options);

        return sendRequest(url, options.getHttpMethod());
    }

    /**
     * Gets the correct URL for HTTP request to Messente's pricing API.
     *
     * @param format The response format. Available formats are XML and JSON.
     * @param country The country code which pricelist you wish to get.
     * @return Correctly formatted URL for HTTP request to Messente's pricing
     * API.
     * @throws MessenteException If country code is not provided.
     */
    public URL getPricingURL(ResponseFormat format, Country country) throws MessenteException {
        return getPricingURL(format, country, null);
    }

    /**
     * Gets the correct URL string for HTTP request to Messente's pricing API.
     *
     * @param format The response format. Available formats are XML and JSON.
     * @param country The country code which pricelist you wish to get.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * pricing API.
     * @throws MessenteException If country code is not provided.
     */
    public String getPricingUrlAsString(ResponseFormat format, Country country) throws MessenteException {
        return getPricingURL(format, country, null).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's pricing API.
     *
     * @param format The response format. Available formats are XML and JSON.
     * @param country The country code which pricelist you wish to get.
     * @param options Makes API call with specific options.
     * @return Correctly formatted URL for HTTP request to Messente's pricing
     * API.
     * @throws MessenteException If country code is not provided.
     */
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

        return buildURL(options.getProtocol(), ApiMethod.PRICES, postData.toString());
    }

    /**
     * Gets the correct URL string for HTTP request to Messente's pricing API.
     *
     * @param format The response format. Available formats are XML and JSON.
     * @param country The country code which pricelist you wish to get.
     * @param options Makes API call with specific options.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * pricing API.
     * @throws MessenteException If country code is not provided.
     */
    public String getPricingUrlAsString(ResponseFormat format, Country country, MessenteOptions options) throws MessenteException {
        return getPricingURL(format, country, options).toString();
    }

    /**
     * Gets account balance. Note that if you send SMS and ask for balance
     * milliseconds after sending, the balance might not be correct. Account
     * balance updating will take a few seconds depending on queues on
     * Messente's side.
     *
     * @return MessenteResponse object with account balance.
     * @throws MessenteException if HTTP request fails.
     */
    public MessenteResponse getBalance() throws MessenteException {
        return getBalance(null);
    }

    /**
     * Gets account balance. Note that if you send SMS and ask for balance
     * milliseconds after sending, the balance might not be correct. Account
     * balance updating will take a few seconds depending on queues on
     * Messente's side.
     *
     * @param options Makes API call with specific options.
     * @return MessenteResponse object with account balance.
     * @throws MessenteException if HTTP request fails.
     */
    public MessenteResponse getBalance(MessenteOptions options) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        URL url = getCreditsURL(options);
        return sendRequest(url, options.getHttpMethod());
    }

    /**
     * Gets the correct URL for HTTP request to Messente's messaging API.
     *
     * @param from Sender ID. Must be registered and validated under your
     * messente.com account.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @return Correctly formatted URL for HTTP request to Messente's messaging
     * API.
     * @throws MessenteException If recipient or SMS text is not specified.
     */
    public URL getMessagingURL(String from, String to, String text) throws MessenteException {
        return getMessagingURL(from, to, text, null);
    }

    /**
     * Gets the correct URL for HTTP request to Messente's messaging API.
     *
     * @param from Sender ID. Must be registered and validated under your
     * messente.com account.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * messaging API.
     * @throws MessenteException If recipient or SMS text is not specified.
     */
    public String getMessagingUrlAsString(String from, String to, String text) throws MessenteException {
        return getMessagingURL(from, to, text, null).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's messaging API. Note
     * that this method will use your default sender ID.
     *
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @return Correctly formatted URL for HTTP request to Messente's messaging
     * API.
     * @throws MessenteException If recipient or SMS text is not specified.
     */
    public URL getMessagingURL(String to, String text) throws MessenteException {
        return getMessagingURL(null, to, text, null);
    }

    /**
     * Gets the correct URL for HTTP request to Messente's messaging API. Note
     * that this method will use your default sender ID.
     *
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * messaging API.
     * @throws MessenteException If recipient or SMS text is not specified.
     */
    public String getMessagingUrlAsString(String to, String text) throws MessenteException {
        return getMessagingURL(null, to, text, null).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's messaging API.
     *
     * @param from Sender ID. Must be registered and validated under your
     * messente.com account.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @param options Makes API call with specific options.
     * @return Correctly formatted URL for HTTP request to Messente's messaging
     * API.
     * @throws MessenteException If recipient or SMS text is not specified.
     */
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

        appendRequestParameter(postData, "to", preparePhoneNumber(to), "UTF-8");
        appendRequestParameter(postData, "text", text, options.getCharset());

        appendRequestParameters(postData, options.getSmsSendingOptions(), true);

        return buildURL(options.getProtocol(), ApiMethod.SEND_SMS, postData.toString());
    }

    /**
     * Gets the correct URL for HTTP request to Messente's messaging API.
     *
     * @param from Sender ID. Must be registered and validated under your
     * messente.com account.
     * @param to Recipient's phone number.
     * @param text SMS text.
     * @param options Makes API call with specific options.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * messaging API.
     * @throws MessenteException If recipient or SMS text is not specified.
     */
    public String getMessagingUrlAsString(String from, String to, String text, MessenteOptions options) throws MessenteException {
        return getMessagingURL(from, to, text, options).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's synchronous delivery
     * report API.
     *
     * @see
     * <a href="http://messente.com/documentation/delivery-report">http://messente.com/documentation/delivery-report</a>
     * @param msgid unique message ID which delivery report you wish to have.
     * @return Correctly formatted URL for HTTP request to Messente's delivery
     * report API.
     * @throws MessenteException Message ID not specified.
     */
    public URL getDlrURL(String msgid) throws MessenteException {
        return getDlrURL(msgid, null);
    }

    /**
     * Gets the correct URL string for HTTP request to Messente's synchronous
     * delivery report API.
     *
     * @see
     * <a href="http://messente.com/documentation/delivery-report">http://messente.com/documentation/delivery-report</a>
     * @param msgid unique message ID which delivery report you wish to have.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * delivery report API.
     * @throws MessenteException Message ID not specified.
     */
    public String getDlrUrlAsString(String msgid) throws MessenteException {
        return getDlrURL(msgid, null).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's synchronous delivery
     * report API.
     *
     * @param options Makes API call with specific options.
     * @see
     * <a href="http://messente.com/documentation/delivery-report">http://messente.com/documentation/delivery-report</a>
     * @param msgid unique message ID which delivery report you wish to have.
     * @return Correctly formatted URL for HTTP request to Messente's delivery
     * report API.
     * @throws MessenteException Message ID not specified.
     */
    public URL getDlrURL(String msgid, MessenteOptions options) throws MessenteException {

        if (msgid == null || msgid.trim().isEmpty()) {
            throw new MessenteException("Message ID not specified!");
        }

        if (options == null) {
            options = new MessenteOptions();
        }

        String postData = "sms_unique_id=" + msgid;
        return buildURL(options.getProtocol(), ApiMethod.GET_DLR_RESPONSE, postData);
    }

    /**
     * Gets the correct URL string for HTTP request to Messente's synchronous
     * delivery report API.
     *
     * @param options Makes API call with specific options.
     * @see
     * <a href="http://messente.com/documentation/delivery-report">http://messente.com/documentation/delivery-report</a>
     * @param msgid unique message ID which delivery report you wish to have.
     * @return Correctly formatted URL string for HTTP request to Messente's
     * delivery report API.
     * @throws MessenteException Message ID not specified.
     */
    public String getDlrUrlAsString(String msgid, MessenteOptions options) throws MessenteException {
        return getDlrURL(msgid, options).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's credits API.
     *
     * @see
     * <a href="http://messente.com/documentation/credits-api">http://messente.com/documentation/credits-api</a>
     * @return Correctly formatted URL for HTTP request to Messente's credits
     * API.
     * @throws MessenteException If building URL fails.
     */
    public URL getCreditsURL() throws MessenteException {
        return getCreditsURL(null);
    }

    /**
     * Gets the correct URL string for HTTP request to Messente's credits API.
     *
     * @see
     * <a href="http://messente.com/documentation/credits-api">http://messente.com/documentation/credits-api</a>
     * @return Correctly formatted URL string for HTTP request to Messente's
     * credits API.
     * @throws MessenteException If building URL fails.
     */
    public String getCreditsUrlAsString() throws MessenteException {
        return getCreditsURL(null).toString();
    }

    /**
     * Gets the correct URL for HTTP request to Messente's credits API.
     *
     * @param options Makes API call with specific options.
     * @see
     * <a href="http://messente.com/documentation/credits-api">http://messente.com/documentation/credits-api</a>
     * @return Correctly formatted URL for HTTP request to Messente's credits
     * API.
     * @throws MessenteException If building URL fails.
     */
    public URL getCreditsURL(MessenteOptions options) throws MessenteException {

        if (options == null) {
            options = new MessenteOptions();
        }

        return buildURL(options.getProtocol(), ApiMethod.GET_BALANCE, null);
    }

    /**
     * Gets the correct URL string for HTTP request to Messente's credits API.
     *
     * @param options Makes API call with specific options.
     * @see
     * <a href="http://messente.com/documentation/credits-api">http://messente.com/documentation/credits-api</a>
     * @return Correctly formatted URL string for HTTP request to Messente's
     * credits API.
     * @throws MessenteException If building URL fails.
     */
    public String getCreditsUrlAsString(MessenteOptions options) throws MessenteException {
        return getCreditsURL(options).toString();
    }

    /**
     * Convenience method for getting the current external IP of the client.
     *
     * @return current external IP address.
     * @throws MessenteException when the IP retrieval failed.
     */
    public String getMyIP() throws MessenteException {

        MessenteResponse response;
        try {
            response = makeHttpRequest(new URL("http://bot.whatismyipaddress.com/"), "GET");
        } catch (MalformedURLException ex) {
            throw new MessenteException("Retrieving IP failed: "
                    + ex.getMessage() != null ? ex.getMessage() : "");
        }

        int respCode = response.getHttpResponseCode();
        if (respCode != 200) {
            throw new MessenteException("Retrieving IP failed with response code "
                    + respCode);
        }

        return response.getRawResponse();
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
