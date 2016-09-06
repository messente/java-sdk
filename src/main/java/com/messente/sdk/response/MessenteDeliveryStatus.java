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
 * Data structure for Messente delivery status.
 *
 * @author Lennar Kallas
 */
public class MessenteDeliveryStatus extends MessenteResponse {

    public static final String SENT = "SENT";
    public static final String DELIVERED = "DELIVERED";
    public static final String FAILED = "FAILED";
    public static final String NO_DLR_YET = "FAILED 102";

    public MessenteDeliveryStatus(String response, int httpCode) {
        super(response, httpCode);
    }

    @Override
    public boolean isSuccess() {
        return (RESPONSE != null
                && !RESPONSE.trim().isEmpty()
                && !RESPONSE.startsWith(ResponsePrefixes.ERROR))
                || RESPONSE.equals(ResponsePrefixes.FAILED + "102");
    }

    @Override
    public String getResult() {

        if (getRawResponse().equals(NO_DLR_YET)) {
            return NO_DLR_YET;
        }

        if (!isSuccess()) {
            return null;
        }

        return getContentAfterPrefix(MessenteResponse.ResponsePrefixes.OK) != null
                ? getContentAfterPrefix(MessenteResponse.ResponsePrefixes.OK) : RESPONSE;
    }

    @Override
    public String toString() {
        return getResult();
    }
}
