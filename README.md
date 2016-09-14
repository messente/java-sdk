# Messente-SDK

Simple and lightweight SDK for using Messente API-s.

Supported API-s and features:

 * Messaging API support
 * Pricing API support
 * Credits API support
 * Delivery report API support
 * SMS length calculator
 * Convenience methods for correct API call URL building

## Installing
Installing messente-sdk is easy. Just add messente-sdk jar to your classpath or use it as maven dependency.
All the needed jar files can be found [here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.messente%22%20AND%20a%3A%22messente-sdk%22).

### With Maven

Just add messente-sdk dependency to your POM.xml file:

```xml
<dependency>
    <groupId>com.messente</groupId>
    <artifactId>messente-sdk</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Usage example

And here's some code for sending SMS! :+1:

```java
public class SendSmsSimpleExample {

    public static final String API_USERNAME = "<api-username-here>";
    public static final String API_PASSWORD = "<api-password-here>";

    public static final String SMS_SENDER_ID = "<your-sender-id-here>";
    public static final String SMS_RECIPIENT = "+3721234567";
    public static final String SMS_TEXT = "Hey! Check out messente.com, it's awesome!";

    public static void main(String[] args) {

        // Create Messente client
        Messente messente = new Messente(API_USERNAME, API_PASSWORD);

        // Create response object
        MessenteResponse response = null;

        try {
            // Send SMS
            response = messente.sendSMS(SMS_SENDER_ID, SMS_RECIPIENT, SMS_TEXT);

            // Checking the response status
            if (response.isSuccess()) {

                // Get Messente server full response
                System.out.println("Server response: " + response.getRawResponse());

                //Get unique message ID part of the response(can be used for retrieving message delivery status later)
                System.out.println("SMS unique ID: " + response.getResult());

            } else {
                // In case of failure get failure message                
                throw new RuntimeException(response.getResponseMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to send SMS! " + e.getMessage());
        }

    }
}
```

Check out **other examples** [on GitHub](https://github.com/messente/messente-sdk/tree/master/examples/src/com/messente/examples).


