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

import com.messente.sdk.enums.Autoconvert;
import com.messente.sdk.enums.Country;
import com.messente.sdk.enums.HttpMethod;
import com.messente.sdk.enums.HttpProtocol;
import com.messente.sdk.enums.ResponseFormat;
import com.messente.sdk.options.MessenteOptions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Messente class. Mainly covers URL building methods.
 *
 * @author Lennar Kallas
 */
public class MessenteTest {

    private final String DUMMY_SENDER = "DummySender";
    private final String DUMMY_RECIPIENT = "+3725123456";
    private final String DUMMY_SMS = "This is a dummy SMS!";

    private final String DUMMY_MSGID = "23i42o35hl3hh352";

    private final String DUMMY_MESSENTE_USER = "dummy_messente_user";
    private final String DUMMY_MESSENTE_PASSWORD = "dummy_messente_password";

    private final String MESSENTE_API_SERVER = "api2.messente.com";
    private final String MESSENTE_API_BACKUP_SERVER = "api3.messente.com";

    private final Messente MESSENTE;

    private final MessenteOptions MESSENTE_OPTIONS = new MessenteOptions.Builder()
            .autoconvert(Autoconvert.ON) // Character replacement setting
            .charset("UTF-8") // Encoding for SMS text and sender ID
            .dlrUrl("http://www.yourdomain.com/process_dlr.php") // Delivery report URL
            .httpMethod(HttpMethod.POST) // HTTP method that is used for API call
            .protocol(HttpProtocol.HTTPS) // HTTP protocol used for API call
            .udh(null) // UDH (User Data Header)
            .validity("60") // For how long message is retried if phone is off (minutes)
            .timeToSend("1453276295") // UNIX timestamp for delayed sending
            .build();   // Finally build options

    public MessenteTest() {
        this.MESSENTE = new Messente(
                DUMMY_MESSENTE_USER,
                DUMMY_MESSENTE_PASSWORD,
                MESSENTE_API_SERVER,
                MESSENTE_API_BACKUP_SERVER);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMessagingURL method, of class Messente.
     */
    @Test
    public void testGetMessagingURL_3args() {

        String expectedUrl = "https://api2.messente.com/send_sms/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&from=" + DUMMY_SENDER
                + "&to=%2B3725123456"
                + "&text=This+is+a+dummy+SMS%21"
                + "&charset=UTF-8";

        try {
            String actualUrl = MESSENTE.getMessagingURL(
                    DUMMY_SENDER,
                    DUMMY_RECIPIENT,
                    DUMMY_SMS)
                    .toString();

            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of getMessagingURL method, of class Messente.
     */
    @Test
    public void testGetMessagingURL_String_String() {

        String expectedUrl = "https://api2.messente.com/send_sms/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&to=%2B3725123456"
                + "&text=This+is+a+dummy+SMS%21"
                + "&charset=UTF-8";
        try {

            String actualUrl = MESSENTE.getMessagingURL(
                    DUMMY_RECIPIENT,
                    DUMMY_SMS)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of getMessagingURL method, of class Messente.
     */
    @Test
    public void testGetMessagingURL_4args() {

        String expectedUrl = "https://api2.messente.com/send_sms/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&from=DummySender"
                + "&to=%2B3725123456"
                + "&text=This+is+a+dummy+SMS%21"
                + "&charset=UTF-8"
                + "&autoconvert=on"
                + "&time_to_send=1453276295"
                + "&dlr-url=http%3A%2F%2Fwww.yourdomain.com%2Fprocess_dlr.php"
                + "&validity=60";

        try {

            String actualUrl = MESSENTE.getMessagingURL(
                    DUMMY_SENDER,
                    DUMMY_RECIPIENT,
                    DUMMY_SMS,
                    MESSENTE_OPTIONS)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of getPricingURL method, of class Messente.
     */
    @Test
    public void testGetPricingURL_MessenteResponseFormat_Country() {

        String expectedUrl = "https://api2.messente.com/prices/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&country=HU"
                + "&format=xml";

        try {

            String actualUrl = MESSENTE.getPricingURL(
                    ResponseFormat.XML,
                    Country.HUNGARY)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getPricingURL method, of class Messente.
     */
    @Test
    public void testGetPricingURL_3args() throws Exception {

        String expectedUrl = "https://api2.messente.com/prices/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&country=HU"
                + "&format=xml";

        try {

            String actualUrl = MESSENTE.getPricingURL(
                    ResponseFormat.XML,
                    Country.HUNGARY,
                    MESSENTE_OPTIONS)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of getDlrURL method, of class Messente.
     */
    @Test
    public void testGetDlrURL_String() {

        String expectedUrl = "https://api2.messente.com/get_dlr_response/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&sms_unique_id=" + DUMMY_MSGID;

        try {

            String actualUrl = MESSENTE.getDlrURL(DUMMY_MSGID)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of getDlrURL method, of class Messente.
     */
    @Test
    public void testGetDlrURL_String_MessenteOptions() {

        String expectedUrl = "https://api2.messente.com/get_dlr_response/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD
                + "&sms_unique_id=" + DUMMY_MSGID;

        try {

            String actualUrl = MESSENTE.getDlrURL(DUMMY_MSGID, MESSENTE_OPTIONS)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of getCreditsURL method, of class Messente.
     */
    @Test
    public void testGetCreditsURL_0args() {

        String expectedUrl = "https://api2.messente.com/get_balance/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD;

        try {

            String actualUrl = MESSENTE.getCreditsURL()
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getCreditsURL method, of class Messente.
     */
    @Test
    public void testGetCreditsURL_MessenteOptions() {

        String expectedUrl = "https://api2.messente.com/get_balance/?"
                + "username=" + DUMMY_MESSENTE_USER
                + "&password=" + DUMMY_MESSENTE_PASSWORD;

        try {

            String actualUrl = MESSENTE.getCreditsURL(MESSENTE_OPTIONS)
                    .toString();

            assertEquals(expectedUrl, actualUrl);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
