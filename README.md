# swissunihockey-game-schedule-pdf-creator
[![Build Status](https://travis-ci.org/rufer7/swissunihockey-game-schedule-pdf-creator.svg)](https://travis-ci.org/rufer7/swissunihockey-game-schedule-pdf-creator)
[![Coverage Status](https://coveralls.io/repos/rufer7/swissunihockey-game-schedule-pdf-creator/badge.svg?branch=master)](https://coveralls.io/r/rufer7/swissunihockey-game-schedule-pdf-creator?branch=master)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/rufer7/swissunihockey-game-schedule-pdf-creator/blob/master/LICENSE)


A web application that generates and serves game schedules as PDF documents


## Swissunihockey API

This application consumes the [swissunihockey API v2](https://api-v2.swissunihockey.ch/api/doc)


## Deployment to cloudfoundry

Execute the following command to deploy the application to cloudfoundry:

`cf push <APP_NAME> -p game-schedule-pdf-generator-VERSION.jar -b https://github.com/cloudfoundry/java-buildpack.git`
