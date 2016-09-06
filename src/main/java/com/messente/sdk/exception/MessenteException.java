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
package com.messente.sdk.exception;

/**
 * Exception wrapped as MessenteException.
 *
 * @author Lennar Kallas
 */
public class MessenteException extends Exception {

    private static final long serialVersionUID = 1997753363232807009L;

    public MessenteException() {
    }

    public MessenteException(String message) {
        super(message);
    }

    public MessenteException(Throwable cause) {
        super(cause);
    }

    public MessenteException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessenteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
