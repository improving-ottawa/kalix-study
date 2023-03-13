# kalix-loadtest

TestMemberByDateTimeQuerySimulation is the primary load test used - all others build up to it.

## To run the tests

- at the shell: > sbt
- sbt:root> project loadtest
- Gatling/testOnly kalix.study.TestGatewaySimulation or GatlingIt/testOnly kalix.study.TestGatewayPurchaseTicketSimulation