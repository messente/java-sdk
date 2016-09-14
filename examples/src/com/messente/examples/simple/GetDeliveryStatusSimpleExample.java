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
import com.messente.sdk.exception.MessenteException;
import com.messente.sdk.options.MessenteOptions;
import com.messente.sdk.response.MessenteDeliveryStatus;

/**
 * Simple example how to get message delivery status using Messente-SDK.
 *
 * @author Lennar Kallas
 */
public class GetDeliveryStatusSimpleExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";

    public static final String MESSAGE_ID = "<message-id-here>";

    public static void main(String[] args) {

        // Create Messente client
        Messente messente = new Messente(API_USERNAME, API_PASSWORD);

        // Create delivery status object
        MessenteDeliveryStatus dlrStatus = null;

        try {

            // #### EXAMPLE 1 ####
            // Requesting SMS delivery status using default request options
            dlrStatus = messente.getDeliveryStatus(MESSAGE_ID);

            // Checking the response status
            if (dlrStatus.isSuccess()) {

                // Get Messente server full response
                System.out.println("Server response: " + dlrStatus.getRawResponse());

                //Get delivery status part of the response
                System.out.println("Delivery status: " + dlrStatus.getResult());

                // Checking statuses
                switch (dlrStatus.getResult()) {
                    case MessenteDeliveryStatus.SENT:
                        // Do something ...
                        break;
                    case MessenteDeliveryStatus.DELIVERED:
                        // Do something ...
                        break;
                    case MessenteDeliveryStatus.FAILED:
                        // Do something ...
                        break;
                    default:
                        System.out.println(dlrStatus.getResponseMessage());
                        break;
                }

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(dlrStatus.getResponseMessage());
            }

            // #### EXAMPLE 2 ####
            // Options object
            MessenteOptions options = new MessenteOptions.Builder()
                    .httpMethod(HttpMethod.GET)
                    .protocol(HttpProtocol.HTTP)
                    .build();

            // Requesting SMS delivery status using predefined options
            dlrStatus = messente.getDeliveryStatus(MESSAGE_ID, options);

            // Checking the response status
            if (dlrStatus.isSuccess()) {

                // Get Messente server full response
                System.out.println("Server response(using options): " + dlrStatus.getRawResponse());

                //Get delivery status part of the response
                System.out.println("Delivery status(using options): " + dlrStatus.getResult());

                // Checking statuses
                switch (dlrStatus.getResult()) {
                    case MessenteDeliveryStatus.SENT:
                        // Do something ...
                        break;
                    case MessenteDeliveryStatus.DELIVERED:
                        // Do something ...
                        break;
                    case MessenteDeliveryStatus.FAILED:
                        // Do something ...
                        break;
                    default:
                        System.out.println(dlrStatus.getResponseMessage());
                        break;
                }

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(dlrStatus.getResponseMessage());
            }

        } catch (MessenteException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to get delivery report! " + e.getMessage());
        }
    }

}
