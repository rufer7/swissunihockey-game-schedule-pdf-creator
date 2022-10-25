# swissunihockey-game-schedule-pdf-creator

[![Build Status](https://travis-ci.com/rufer7/swissunihockey-game-schedule-pdf-creator.svg?branch=master)](https://travis-ci.com/rufer7/swissunihockey-game-schedule-pdf-creator)
[![Coverage Status](https://coveralls.io/repos/rufer7/swissunihockey-game-schedule-pdf-creator/badge.svg?branch=master)](https://coveralls.io/r/rufer7/swissunihockey-game-schedule-pdf-creator?branch=master)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/rufer7/swissunihockey-game-schedule-pdf-creator/blob/master/LICENSE)

A web application that generates and serves swiss unihockey game schedules as PDF documents

## Swiss unihockey API

This application consumes the [swiss unihockey API v2](https://api-v2.swissunihockey.ch/api/doc)

## App information

To get information about the application, call the following endpoint.

`.../info`

## Deployment to Azure

### Prerequisites

- Azure tenant
- Azure subscription

### Step by Step Manual

1. Create a new resource group (i.e. `swuh-p1-rg-pdfcreator`)
1. Create a new app service plan in the before created resource group (i.e. `swuh-p1-appplan-pdfcreator`)

    - Operating system: `Linux`
    - Pricing tier: `B1`

1. Create a new web app (i.e. `swuh-p1-appsrv-pdfcreator`)

    - Publish: `Code`
    - Runtime stack: `Java 8`
    - Java web server stack: `Java SE (Embedded Web Server)`
    - Operating system: `Linux`
    - Linux Plan: select before created app service plan
    - Enable and configure continuous deployment under `Deployment` tab
