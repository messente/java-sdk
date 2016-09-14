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
import com.messente.sdk.enums.Country;
import com.messente.sdk.enums.HttpMethod;
import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.enums.ResponseFormat;
import com.messente.sdk.exception.MessenteException;
import com.messente.sdk.options.MessenteOptions;
import com.messente.sdk.response.MessenteResponse;

/**
 *
 * Simple example how to get price list for desired country using Messente-SDK.
 *
 * @author Lennar Kallas
 */
public class GetPricesSimpleExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";

    public static void main(String[] args) {

        // Create Messente client
        Messente messente = new Messente(API_USERNAME, API_PASSWORD);

        // Create response object
        MessenteResponse response = null;

        try {
            // #### EXAMPLE 1 ####
            // Get price list for Hungary in default response format which is JSON, using default request options.
            response = messente.getPriceList(Country.HUNGARY);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 1 ####");

                System.out.println("\nDefault(JSON) response format:\n");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get account balance part of the response
                System.out.println("Prices: " + response.getResult());

                //As you can see the outcome of getResponse and getResult methods are the same so you can use either one of them.
            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }

            // #### EXAMPLE 2 ####
            // Get price list for Hungary in XML response format using default request options.
            response = messente.getPriceList(Country.HUNGARY, ResponseFormat.XML);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 2 ####");

                System.out.println("\nXML response format:\n");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get account balance part of the response
                System.out.println("Prices: " + response.getResult());

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }
            // #### EXAMPLE 3 ####
            // Create SMS options object
            MessenteOptions options = new MessenteOptions.Builder()
                    .httpMethod(HttpMethod.POST) // HTTP method that is used for API call
                    .protocol(HttpProtocol.HTTPS) // HTTP protocol used for API call
                    .build();   // Finally build options

            // Get price list for Hungary in XML response format using predefined request options.
            response = messente.getPriceList(Country.HUNGARY, ResponseFormat.XML, options);

            // Checking the response status
            if (response.isSuccess()) {

                System.out.println("#### EXAMPLE 3 ####");

                System.out.println("\nXML response format:\n");

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get account balance part of the response
                System.out.println("Prices: " + response.getResult());

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }

        } catch (MessenteException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to get prices! " + e.getMessage());
        }

    }

}
