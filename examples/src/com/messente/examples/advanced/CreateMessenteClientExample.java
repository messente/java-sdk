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
import com.messente.sdk.exception.MessenteException;

/**
 * Example of different ways to create Messente client.
 *
 * @author Lennar Kallas
 */
public class CreateMessenteClientExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";

    public static final String SERVER = "api2.messente.com";
    public static final String BACKUP_SERVER = "api3.messente.com";

    public static void main(String[] args) {

        // Example 1 - the "usual" way of instantiating Messente client object
        Messente messente1 = new Messente(API_USERNAME, API_PASSWORD);

        // Example 2 - specify server and backup server URL (these don't change but just in case they do)
        Messente messente2 = new Messente(API_USERNAME, API_PASSWORD, SERVER, BACKUP_SERVER);

        try {
            // Example 3 - instantiating Messente client object by passing a properties file path to the constructor
            Messente messente3 = new Messente("/valid/path/to/messente.properties");
        } catch (MessenteException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException("Failed to instantiate Messente client!");
        }
    }

}
