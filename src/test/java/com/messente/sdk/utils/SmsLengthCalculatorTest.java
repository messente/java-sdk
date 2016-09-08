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
package com.messente.sdk.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for public methods in SmsLengthCalculator class.
 *
 * @author Lennar Kallas
 */
public class SmsLengthCalculatorTest {

    private final SmsLengthCalculator SMS_CALCULATOR;

    private final String GSM_7BIT_SMS = "This SMS contains only characters that are present in GSM charset!";
    private final String GSM_7BITEXT_SMS = "This SMS contains some GSM charset characters that must be escaped like: €, [, ] etc.";
    private final String UNICODE_SMS = "This SMS contains a unicode character: Õ";
    private final String UNICODE_7BITEXT_SMS = "This SMS contains a unicode character Õ and GSM charset character € that needs to be escaped.";

    public SmsLengthCalculatorTest() {
        this.SMS_CALCULATOR = new SmsLengthCalculator();
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
     * Test of getCharset method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetGsm7bitCharset() {

        int charset = SMS_CALCULATOR.getCharset(GSM_7BIT_SMS);

        assertTrue(0 == charset);
    }

    /**
     * Test of getCharset method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetUnicodeCharset() {

        int charset = SMS_CALCULATOR.getCharset(UNICODE_SMS);

        assertTrue(2 == charset);
    }

    /**
     * Test of getCharset method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetGsm7bitExtCharset() {

        int charset = SMS_CALCULATOR.getCharset(GSM_7BITEXT_SMS);

        assertTrue(0 == charset);
    }

    /**
     * Test of getCharset method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetGsm7bitExtUnicodeCharset() {

        int charset = SMS_CALCULATOR.getCharset(UNICODE_7BITEXT_SMS);

        assertTrue(2 == charset);
    }

    /**
     * Test of getPartCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetPartCountGsm7bit() {

        int calculatedParts = SMS_CALCULATOR.getPartCount(GSM_7BIT_SMS);
        assertTrue(1 == calculatedParts);
    }

    /**
     * Test of getPartCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetPartCountGsm7bitExt() {

        int calculatedParts = SMS_CALCULATOR.getPartCount(GSM_7BITEXT_SMS);

        assertTrue(1 == calculatedParts);
    }

    /**
     * Test of getPartCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetPartCountUnicodeSms() {

        int calculatedParts = SMS_CALCULATOR.getPartCount(UNICODE_SMS);

        assertTrue(1 == calculatedParts);
    }

    /**
     * Test of getPartCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetPartCountUnicode7bitExtSms() {

        int calculatedParts = SMS_CALCULATOR.getPartCount(UNICODE_7BITEXT_SMS);

        assertTrue(2 == calculatedParts);
    }

    /**
     * Test of getCharacterCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetCharacterCount7bitSms() {
        int length = GSM_7BIT_SMS.length();
        int calculatedLength = SMS_CALCULATOR.getCharacterCount(GSM_7BIT_SMS);

        assertTrue(length == calculatedLength);

    }

    /**
     * Test of getCharacterCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetCharacterCount7bitExtSms() {

        int calculatedLength = SMS_CALCULATOR.getCharacterCount(GSM_7BITEXT_SMS);

        assertTrue(88 == calculatedLength);

    }

    /**
     * Test of getCharacterCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetCharacterCountUnicodeSms() {

        int calculatedLength = SMS_CALCULATOR.getCharacterCount(UNICODE_SMS);

        assertTrue(40 == calculatedLength);

    }

    /**
     * Test of getCharacterCount method, of class SmsLengthCalculator.
     */
    @Test
    public void testGetCharacterCountUnicode7bitExtSms() {

        int calculatedLength = SMS_CALCULATOR.getCharacterCount(UNICODE_7BITEXT_SMS);

        assertTrue(93 == calculatedLength);

    }

}
