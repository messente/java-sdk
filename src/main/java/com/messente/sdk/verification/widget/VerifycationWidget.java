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
package com.messente.sdk.verification.widget;

import com.messente.sdk.exception.MessenteException;

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Lennar Kallas
 */
public class VerifycationWidget {

    /**
     * List of allowed parameters for signature building.
     */
    private final List<String> ALLOWED_PARAMS = Arrays.asList(
            "user",
            "phone",
            "version",
            "callback_url",
            "sig",
            "status");

    /**
     * Generate signature.
     *
     * @param parameters Map with request parameters.
     * @param pass Messente account API password.
     * @return MD5 hashed string as signature.
     * @throws com.messente.sdk.exception.MessenteException
     */
    public String generateSignature(Map<String, String> parameters, String pass) throws MessenteException {

        if (parameters == null || parameters.isEmpty()) {
            throw new MessenteException("Parameters are missing - can't generate signature!");
        }

        if (!parameters.containsKey("user") && parameters.get("user").trim().isEmpty()) {
            throw new MessenteException("'user' parameter is missing - can't generate signature!");
        }

        if (pass == null || pass.trim().isEmpty()) {
            throw new MessenteException("Password not set - can't generate signature!");
        }

        if (!parameters.containsKey("version") && parameters.get("version").trim().isEmpty()) {
            throw new MessenteException("'version' parameter is missing - can't generate signature!");
        }

        parameters.put("pass", pass);

        return generateSignatureHash(parameters);
    }

    /**
     * Compares signatures.
     *
     * @param parameters Map with request parameters.
     * @param pass Messente account API password.
     * @return true if signatures are equal, otherwise false.
     * @throws com.messente.sdk.exception.MessenteException
     */
    public boolean verifySignature(Map<String, String> parameters, String pass) throws MessenteException {

        if (parameters == null || parameters.isEmpty()) {
            throw new MessenteException("Parameters are missing - can't compare signatures!");
        }

        if (!parameters.containsKey("sig") && parameters.get("sig").trim().isEmpty()) {
            throw new MessenteException("'sig' parameter is missing - can't compare signatures!");
        }

        if (pass == null || pass.trim().isEmpty()) {
            throw new MessenteException("Password not set - can't compare signatures!");
        }

        return parameters.get("sig").equals(generateSignature(parameters, pass));
    }

    /**
     * Generates string that is a MD5 hash.
     *
     * @param parameters Map with request parameters.
     * @return unique signature.
     */
    private String generateSignatureHash(Map<String, String> parameters) throws MessenteException {

        // Add everything to sorted map
        TreeMap<String, String> sortedParams = new TreeMap<>(parameters);

        StringBuilder sigString = new StringBuilder();

        for (Map.Entry<String, String> item : sortedParams.entrySet()) {
            String key = item.getKey();
            String value = item.getValue();

            if ((ALLOWED_PARAMS.contains(key) && !key.equalsIgnoreCase("sig"))
                    || key.equalsIgnoreCase("pass")) {

                sigString
                        .append(key)
                        .append(value);
            }
        }

        return generateHash(sigString.toString());
    }

    /**
     * Generates MD5 signature hash.
     *
     * @param unhashed String that needs to be hashed.
     * @return hashed string.
     */
    private String generateHash(String unhashed) throws MessenteException {

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(unhashed.getBytes("UTF-8"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new MessenteException("Hashing failed - " + ex.getMessage());
        }
    }

}
