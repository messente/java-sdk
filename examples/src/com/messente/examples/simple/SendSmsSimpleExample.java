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
import com.messente.sdk.enums.Autoconvert;
import com.messente.sdk.enums.HttpMethod;
import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.options.MessenteOptions;
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
            // #### EXAMPLE 1 ###
            // Send SMS with default sender ID and default request parameters
            response = messente.sendSMS(SMS_RECIPIENT, SMS_TEXT);

            // Checking the response status
            if (response.isSuccess()) {
                System.out.println("#### EXAMPLE 1 ####");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get unique message ID part of the response(can be used for retrieving message delivery status later)
                System.out.println("SMS unique ID: " + response.getResult());

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }

            // #### EXAMPLE 2 ###
            // Send SMS with given sender ID and default request parameters
            response = messente.sendSMS(SMS_SENDER_ID, SMS_RECIPIENT, SMS_TEXT);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 2 ####");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get unique message ID part of the response(can be used for retrieving message delivery status later)
                System.out.println("SMS unique ID: " + response.getResult());

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }

            // #### EXAMPLE 3 ###
            // Send SMS with given sender ID and predefined request parameters
            // Create SMS options object that you can reuse for each SMS you are sending
            MessenteOptions options = new MessenteOptions.Builder()
                    .autoconvert(Autoconvert.ON) // Character replacement setting
                    .charset("UTF-8") // Encoding for SMS text and sender ID
                    .dlrUrl("http://yourdomain.com/dlr_handling_script.php") // Delivery report URL
                    .httpMethod(HttpMethod.POST) // HTTP method that is used for API call
                    .protocol(HttpProtocol.HTTPS) // HTTP protocol used for API call
                    .udh(null) // UDH (User Data Header)
                    .validity("60") // For how long message is retried if phone is off
                    .timeToSend("1453276295") // UNIX timestamp for delayed sending
                    .build();   // Finally build options                    .

            response = messente.sendSMS(SMS_SENDER_ID, SMS_RECIPIENT, SMS_TEXT, options);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 3 ####");

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
