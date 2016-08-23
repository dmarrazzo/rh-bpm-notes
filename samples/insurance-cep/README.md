# Complex Event Processing Sample (Insurance Scenario)

This sample is based on a really simplistic insurance scenario.

From one of the customer relationship channels, you get the following events:

1. QuoteRequest
2. Buy

## The demo logic

Event Rules:

1. When a BASIC customer places a QuoteRequest that is not followed by a Buy (policy signature) in 120 minutes, send a reminder email
2. When a GOLDEN customer places an high value QuoteRequest (>1000) that is not followed by a Buy (policy signature) in 120 minutes, call him back
3. When a BASIC customer places more than 2 QuoteRequest in 3 hours, call him back

## Running the demo

In order to test the 3 rules, you have to edit and run QuoteRequestTest.java:

1. Run it AS-IS, the first rule will be triggered
2. Increase the value of the last QuoteRequest to test the second rule.  
   E.g. `qr = new QuoteRequest("GG222ZZ", 1400, clock.getCurrentTime());`

3. Reduce the time interval between QuoteRequest to trigger the third rule  
   E.g. `clock.advanceTime(15, TimeUnit.MINUTES);` 