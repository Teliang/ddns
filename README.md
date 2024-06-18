# ddns
DDNS (Dynamic DNS), Support NameSilo

## Overview

It allows you to dynamically update your DNS records based on your current public IP address.

## Prerequisites

- Java JDK 17+
- Maven

## Running the Application
```sh
mvn package

java -jar target/ddns-0.0.1-SNAPSHOT-jar-with-dependencies.jar --domain=YOURDOMAIN --key=YOURKEY
```

