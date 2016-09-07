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
package com.messente.sdk.response;

/**
 * Data structure for holding Messente API responses.
 *
 * @author Lennar Kallas
 */
public class MessenteResponse {

    protected final String RESPONSE;
    protected final int HTTP_RESPONSE_CODE;

    public static final String ACCESS_RESTRICTED = "Access is restricted, "
            + "wrong credentials. Check the username and password values.";

    public static final String PARAMETERS_WRONG_OR_MISSING = "Parameters are "
            + "wrong or missing. Check that all the required parameters are present.";

    public static final String INVALID_IP = "Invalid IP address. The IP address "
            + "you made the request from, is not in the API settings whitelist.";

    public static final String UNKNOWN_COUNTRY = "Country was not found.";

    public static final String COUNTRY_NOT_SUPPORTED = "This country is not supported";

    public static final String INVALID_FORMAT = "Invalid format provided - "
            + "only json or xml is allowed.";

    public static final String UNKNOWN_MESSAGE_ID = "Could not find the "
            + "message with sms_unique_id";

    public static final String BLACKLISTED_NR = "Number is in blacklist.";

    public static final String INVALID_SENDER = "Sender parameter \"from\" "
            + "is invalid. You have not activated this sender name on messente.com";

    public static final String NO_DLR = "No Delivery report yet, "
            + "try again later.";

    public static final String SERVER_FAILURE = "Server failure. Try again "
            + "after a few seconds or try the api3.messente.com backup server. ";

    public static final String DLR_SENT = "Message has been submitted but does "
            + "not yet have any delivery information";

    public static final String DLR_FAILED = "Message delivery failed!";

    public static final String DLR_DELIVERED = "SMS was successfully delivered "
            + "to recipient";

    public MessenteResponse(String response, int httpCode) {
        this.RESPONSE = response;
        this.HTTP_RESPONSE_CODE = httpCode;
    }

    /**
     * Gets the unmodified server response.
     *
     * @return Server response as string.
     */
    public String getRawResponse() {
        return RESPONSE;
    }

    /**
     * Gets the HTTP response code.
     *
     * @return HTTP response code.
     */
    public int getHttpResponseCode() {
        return HTTP_RESPONSE_CODE;
    }

    /**
     * Get whether the response is a successful one.
     *
     * @return true if the API call was successful (without prefix ERROR or
     * FAILED).
     */
    public boolean isSuccess() {

        return RESPONSE != null
                && !RESPONSE.trim().isEmpty()
                && !RESPONSE.startsWith(ResponsePrefixes.ERROR)
                && !RESPONSE.startsWith(ResponsePrefixes.FAILED);
    }

    /**
     * Private class with response prefix constants.
     */
    protected static class ResponsePrefixes {

        public static final String OK = "OK ";
        public static final String ERROR = "ERROR ";
        public static final String FAILED = "FAILED ";
    }

    /**
     * Gets the result of successful response. Useful for getting Unique message
     * ID or DLR status.
     *
     * @return null if the response was not successful, otherwise string without
     * response prefix ("OK").
     */
    public String getResult() {

        if (!isSuccess()) {
            return null;
        }

        return getContentAfterPrefix(ResponsePrefixes.OK) != null
                ? getContentAfterPrefix(ResponsePrefixes.OK) : RESPONSE;
    }

    /**
     * Gets the substring after defined response prefix.
     *
     * @param prefix
     * @return null if input prefix is not defined, otherwise substring after
     * prefix.
     */
    protected String getContentAfterPrefix(String prefix) {
        if (this.RESPONSE != null && this.RESPONSE.startsWith(prefix)) {
            return this.RESPONSE.substring(prefix.length());
        }
        return null;
    }

    /**
     * Gets explanatory message of the response.
     *
     * @return Explanation of the failure reason or DLR status.
     */
    public String getResponseMessage() {

        // No failure to report
        if (isSuccess()) {
            return getDlrMessage();
        } else {
            return getFailureMessage();
        }

    }

    /**
     *
     * @return string with DLR status explanation.
     */
    protected String getDlrMessage() {
        switch (getRawResponse()) {
            case "OK SENT":
                return DLR_SENT;
            case "OK FAILED":
                return DLR_FAILED;
            case "OK DELIVERED":
                return DLR_DELIVERED;
            case "FAILED 102":
                return NO_DLR;
        }
        return null;
    }

    /**
     *
     * @return string with failure explanation.
     */
    protected String getFailureMessage() {
        switch (getRawResponse()) {
            case "ERROR 101":
                return MessenteResponse.ACCESS_RESTRICTED;
            case "ERROR 102":
                return MessenteResponse.PARAMETERS_WRONG_OR_MISSING;
            case "ERROR 103":
                return MessenteResponse.INVALID_IP;
            case "ERROR 104":
                return MessenteResponse.UNKNOWN_COUNTRY;
            case "ERROR 105":
                return MessenteResponse.COUNTRY_NOT_SUPPORTED;
            case "ERROR 107":
                return MessenteResponse.UNKNOWN_MESSAGE_ID;
            case "ERROR 108":
                return MessenteResponse.BLACKLISTED_NR;
            case "ERROR 111":
                return MessenteResponse.INVALID_SENDER;
            case "FAILED 102":
                return MessenteResponse.NO_DLR;
            case "FAILED 209":
                return MessenteResponse.SERVER_FAILURE;
        }
        return RESPONSE;
    }

    @Override
    public String toString() {
        return "Server response: " + RESPONSE + "\n"
                + "Response message: " + getResponseMessage();
    }

}
