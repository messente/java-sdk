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
package com.messente.sdk.enums;

/**
 * Enumeration of Messente API methods.
 *
 * @see
 * <a href="http://messente.com/documentation/setup-and-activation">Messente API
 * doc.</a>
 * @author Lennar Kallas
 */
public enum ApiMethod {

    SEND_SMS("/send_sms/"),
    GET_DLR_RESPONSE("/get_dlr_response/"),
    PRICES("/prices/"),
    GET_BALANCE("/get_balance/"),
    VERIFY_START("/verify/start/"),
    VERIFY_PIN("/verify/pin/");

    private final String apiMethod;

    private ApiMethod(final String apiMethod) {
        this.apiMethod = apiMethod;
    }

    @Override
    public String toString() {
        return apiMethod;
    }

}
