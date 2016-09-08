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
package com.messente.examples.advanced;

import com.messente.sdk.Messente;
import com.messente.sdk.enums.Country;
import com.messente.sdk.enums.ResponseFormat;
import com.messente.sdk.exception.MessenteException;

import java.net.URL;

/**
 * This is an example of how to get URL -s for API calls. This is useful when
 * you decide to use some HTTP client library for HTTP/HTTPS requests.
 *
 * @author Lennar Kallas
 */
public class BuildUrlExamples {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";

    public static final String SMS_SENDER_ID = "<your-sender-id-here>";
    public static final String SMS_RECIPIENT = "+372512345678";
    public static final String SMS_TEXT = "Hey! Check out messente.com, it's awesome!";

    public static final String MESSAGE_ID = "api25eb1e22fb3bf041c9b6cd12cd15d682545454367";

    public static void main(String[] args) {

        // Create Messente client
        Messente messente = new Messente(API_USERNAME, API_PASSWORD);

        try {

            // Credits URL
            URL creditsUrl = messente.getCreditsURL();
            System.out.println(creditsUrl);

            // Delivery report URL
            URL dlrUrl = messente.getDlrURL(MESSAGE_ID);
            System.out.println(dlrUrl);

            // Pricing URL
            URL pricingUrl = messente.getPricingURL(ResponseFormat.XML, Country.AUSTRALIA);
            System.out.println(pricingUrl);

            // Messaging URL
            URL messagingUrl = messente.getMessagingURL(SMS_SENDER_ID, SMS_RECIPIENT, SMS_TEXT);
            System.out.println(messagingUrl);

        } catch (MessenteException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException("Failed to build URL!");
        }

    }

}
