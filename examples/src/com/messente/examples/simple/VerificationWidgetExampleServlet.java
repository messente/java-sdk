/*
 * Copyright 2016 Messente Communications.
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

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.messente.sdk.utils.VerificationWidgetHelper;

/**
 * Example servlet for handling Messente's verification widget response.
 *
 * @see <a href="http://messente.com/documentation/verification-widget">Read
 * more here.</a>
 * @author Lennar Kallas
 */
public class VerificationWidgetExampleServlet extends HttpServlet {

    // Example credentials
    private final String MESSENTE_API_USERNAME = "your_username_here";
    private final String MESSENTE_API_PASSWORD = "your_password_here";
    private final String PHONE_NR = "+372512345678";
    private final String VERSION = "1";
    private final String CALLBACK_URL = "http://yourserver.com/verification/";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException,
            IOException {

        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException,
            IOException {

        // Get the map with request parameters
        Map<String, String[]> reqParams = request.getParameterMap();

        if (reqParams.isEmpty()) {
            return;
        }
        // Create map with strings
        Map<String, String> params = new HashMap<>();

        for (Map.Entry<String, String[]> entry : reqParams.entrySet()) {
            String key = entry.getKey();
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "");

            // For debugging
            System.out.println(key + " -> " + value);
            params.put(key, value);
        }

        // Response text
        String respStr = null;

        if (params.containsKey("status")
                && params.get("status").equalsIgnoreCase("VERIFIED")) {

            // You may want to compare phone numbers before allowing the user in
            if (!params.get("phone").equalsIgnoreCase(PHONE_NR)) {

                respStr = "Phone number that you used for authenticating doesn't "
                        + "match with the one registered in your account!";

                System.out.println("VERIFICATION ERROR: Phone number does not match!");
            } else {

                // Initialize widgetHelper
                VerificationWidgetHelper widgetHelper = new VerificationWidgetHelper();

                // Verify signatures by comparing signatures that Messente returned (param 'sig') 
                // and the signature you can calculate from response parameters
                respStr = widgetHelper.verifySignature(params, MESSENTE_API_PASSWORD)
                        ? "You are verified!" : "Sorry! Verification failed!";
            }

        } else {
            respStr = "Oops! Something went wrong! Verification status "
                    + params.get("status");
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(respStr);

    }

}
