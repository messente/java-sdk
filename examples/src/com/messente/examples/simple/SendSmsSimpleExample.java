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
import com.messente.sdk.response.MessenteResponse;

/**
 * Simple example how to send SMS using Messente-SDK.
 *
 * @author Lennar Kallas
 */
public class SendSmsSimpleExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";

    public static final String SMS_SENDER_ID = "<your-sender-id-here>";
    public static final String SMS_RECIPIENT = "+3721234567";
    public static final String SMS_TEXT = "Hey! Check out messente.com, it's awesome!";

    public static void main(String[] args) {

        // Create Messente client
        Messente messente = new Messente(API_USERNAME, API_PASSWORD);

        // Create response object
        MessenteResponse response = null;

        try {
            // Send SMS
            response = messente.sendSMS(SMS_SENDER_ID, SMS_RECIPIENT, SMS_TEXT);

            // Checking the response status
            if (response.isSuccess()) {

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get unique message ID part of the response(can be used for retrieving message delivery status later)
                System.out.println("SMS unique ID: " + response.getResult());

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
