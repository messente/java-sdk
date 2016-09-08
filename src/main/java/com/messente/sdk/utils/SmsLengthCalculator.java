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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * SMS length calculator.
 *
 * SMS length calculation class that detects if the message can be sent in GSM
 * charset or UCS-2 must be used. Also takes care of trailing escape character
 * if a message is split on extended character.
 *
 * @see
 * <a href="http://messente.com/documentation/sms-length-calculator">http://messente.com/documentation/sms-length-calculator</a>
 *
 * @author Jaanus Rõõmus
 */
public class SmsLengthCalculator {

    private final int GSM_CHARSET_7BIT = 0;
    private final int GSM_CHARSET_UNICODE = 2;
    private final char GSM_7BIT_ESC = '\u001b';

    /**
     * List of all characters supported by GSM charset.
     */
    private final Set<String> GSM7BIT = new HashSet<>(Arrays.asList(
            new String[]{
                "@", "£", "$", "¥", "è", "é", "ù", "ì", "ò", "Ç", "\n", "Ø", "ø", "\r", "Å", "å",
                "Δ", "_", "Φ", "Γ", "Λ", "Ω", "Π", "Ψ", "Σ", "Θ", "Ξ", "\u001b", "Æ", "æ", "ß", "É",
                " ", "!", "'", "#", "¤", "%", "&", "\"", "(", ")", "*", "+", ",", "-", ".", "/",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?",
                "¡", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ñ", "Ü", "§",
                "¿", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "ä", "ö", "ñ", "ü", "à"
            }
    ));

    /**
     * Set of extended characters that must be escaped with '\u001b'.
     */
    private final Set<String> GSM7BITEXT = new HashSet<>(Arrays.asList(
            new String[]{
                "\f", "^", "{", "}", "\\", "[", "~", "]", "|", "€"
            }
    ));

    /**
     * Gets the charset that is used in SMS text.
     *
     * @param content SMS text.
     * @return integer type. returns 0 if all characters are present in GSM
     * charset or 2 if message must be sent in Unicode (UCS-2) charset instead.
     */
    public int getCharset(String content) {

        for (int i = 0; i < content.length(); i++) {
            if (!GSM7BIT.contains(Character.toString(content.charAt(i)))) {
                if (!GSM7BITEXT.contains(Character.toString(content.charAt(i)))) {
                    return GSM_CHARSET_UNICODE;
                }
            }
        }

        return GSM_CHARSET_7BIT;

    }

    /**
     * Gets SMS message parts count for SMS text that contains 7-bit characters.
     *
     * @param content SMS text.
     * @return SMS message part count.
     */
    private int getPartCount7bit(String content) {

        StringBuilder content7bit = new StringBuilder();

        // Add escape characters for extended charset
        for (int i = 0; i < content.length(); i++) {
            if (!GSM7BITEXT.contains(content.charAt(i) + "")) {
                content7bit.append(content.charAt(i));
            } else {
                content7bit.append('\u001b');
                content7bit.append(content.charAt(i));
            }
        }

        if (content7bit.length() <= 160) {

            return 1;

        } else {

            // Start counting the number of messages
            int parts = (int) Math.ceil(content7bit.length() / 153.0);
            int free_chars = content7bit.length() - (int) Math.floor(content7bit.length() / 153.0) * 153;

            // We have enough free characters left, don't care about escape character at the end of sms part
            if (free_chars >= parts - 1) {
                return parts;
            }

            // Reset counter
            parts = 0;
            while (content7bit.length() > 0) {

                // Advance sms counter
                parts++;

                // Check for trailing escape character
                if (content7bit.length() >= 152 && content7bit.charAt(152) == GSM_7BIT_ESC) {
                    content7bit.delete(0, 152);
                } else {
                    content7bit.delete(0, 153);
                }

            }

            return parts;
        }

    }

    /**
     * Number of parts this message is split up to when sent via SMS.
     *
     * @param content SMS text.
     *
     * @return -1 If charset cannot be defined. Otherwise the number of SMS
     * parts.
     */
    public int getPartCount(String content) {

        int charset = getCharset(content);

        if (charset == GSM_CHARSET_7BIT) {

            return this.getPartCount7bit(content);

        } else if (charset == GSM_CHARSET_UNICODE) {

            if (content.length() <= 70) {
                return 1;
            } else {
                return (int) Math.ceil(content.length() / 67.0);
            }

        }

        return -1;

    }

    /**
     * Gets the SMS text character count.
     *
     * @param content SMS text.
     * @return The character count this SMS text consists of.
     */
    public int getCharacterCount(String content) {

        int chars = 0;

        for (int i = 0; i < content.length(); i++) {
            if (GSM7BIT.contains(content.charAt(i) + "")) {
                chars++;
            } else if (GSM7BITEXT.contains(content.charAt(i) + "")) {
                chars += 2;
            } else {
                chars = content.length();
                break;
            }
        }

        return chars;
    }
}
