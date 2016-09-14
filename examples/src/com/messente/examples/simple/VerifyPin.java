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
package com.messente.examples.simple;

import com.messente.sdk.Messente;
import com.messente.sdk.enums.HttpMethod;
import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.options.MessenteOptions;
import com.messente.sdk.response.MessenteResponse;

/**
 * Simple example how to verify PIN using Messente-SDK.
 *
 * @author Lennar Kallas
 */
public class VerifyPin {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";
    public static final String SMS_RECIPIENT = "+3721234567";

    public static final String VERIFICATION_ID = "<verification-id>";
    public static final String PIN = "<pin-user-entered-on-your-page>";

    public static void main(String[] args) {

        // Create Messente client
        Messente messente = new Messente(API_USERNAME, API_PASSWORD);

        // Create response object
        MessenteResponse response = null;

        try {
            // #### EXAMPLE 1 ####
            // Verify PIN with default options
            response = messente.verifyPin(VERIFICATION_ID, PIN);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 1 ####");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                // Get result INVALID/EXPIRED/THROTTLED
                System.out.println("Verification result: " + response.getResult());

                // Handle the result
                switch (response.getResult()) {
                    case "VERIFIED":
                        // Do something...
                        break;
                    case "EXPIRED":
                        // Do something...
                        break;
                    case "THROTTLED":
                        // Do something...
                        break;
                }

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }

            // #### EXAMPLE 2 ####
            // Verify PIN with predefined default options
            // Create options object
            MessenteOptions options = new MessenteOptions.Builder()
                    .httpMethod(HttpMethod.GET)
                    .protocol(HttpProtocol.HTTP)
                    .ip("<your-ip-address>")
                    .browser("<your-browser>")
                    .build();

            response = messente.verifyPin(VERIFICATION_ID, PIN, options);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 2 ####");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                // Get result INVALID/EXPIRED/THROTTLED
                System.out.println("Verification result: " + response.getResult());

                // Handle the result
                switch (response.getResult()) {
                    case "VERIFIED":
                        // Do something...
                        break;
                    case "EXPIRED":
                        // Do something...
                        break;
                    case "THROTTLED":
                        // Do something...
                        break;
                }

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to send SMS! " + e.getMessage());
        }

    }
}
