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

import com.messente.sdk.utils.SmsLengthCalculator;

/**
 * SMS calculator usage example.
 *
 * @author Lennar Kallas
 */
public class SmsCalculatorExample {

    private static final SmsLengthCalculator SMS_CALCULATOR = new SmsLengthCalculator();
    private static final String SMS_TEXT = ""
            + "The morpheme can be reduplicated "
            + "to emphasize the meaning of the word!";

    public static void main(String[] args) {
        int smsParts = SMS_CALCULATOR.getPartCount(SMS_TEXT);
        int smsLength = SMS_CALCULATOR.getCharacterCount(SMS_TEXT);

        System.out.println("Message with text [" + SMS_TEXT + "] "
                + "contains [" + smsLength + " characters] "
                + "and is sent as [" + smsParts + " SMS]");
    }

}
