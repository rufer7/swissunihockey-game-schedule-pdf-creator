# swissunihockey-game-schedule-pdf-creator
[![Build Status](https://travis-ci.org/rufer7/swissunihockey-game-schedule-pdf-creator.svg)](https://travis-ci.org/rufer7/swissunihockey-game-schedule-pdf-creator)
[![Coverage Status](https://coveralls.io/repos/rufer7/swissunihockey-game-schedule-pdf-creator/badge.svg?branch=master)](https://coveralls.io/r/rufer7/swissunihockey-game-schedule-pdf-creator?branch=master)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/rufer7/swissunihockey-game-schedule-pdf-creator/blob/master/LICENSE)


A web application that generates and serves swiss unihockey game schedules as PDF documents


## Swiss unihockey API

This application consumes the [swiss unihockey API v2](https://api-v2.swissunihockey.ch/api/doc)


## Deployment to Heroku

Application will be deployed autmatically on every commit to master (i.e. release)

For information about the deployment to heroku have a look at [Getting Started with Java on Heroku](https://devcenter.heroku.com/articles/getting-started-with-java#set-up)

## Deployment to cloudfoundry

### Maven

The application can be automatically deployed to cloudfoundry with the [cf-maven-plugin](https://github.com/cloudfoundry/cf-java-client/tree/master/cloudfoundry-maven-plugin). To do so build the application (`clean` `install`) with the maven profile `deploy-to-cloudfoundry`. As a prerequisite the server has to be specified in the `settings.xml`(`~\m2\settings.xml`) file.

```
    <settings>
        ...
	    <servers>
	        ...
	        <server>
	          <id>swisscom-cloudfoundry</id>
	          <username>USERNAME</username>
	          <password>PASSWORD</password>
	        </server>
	    </servers>
	    ...
    </settings>
```


### Manual

Execute the following command to deploy the application to cloudfoundry:

`cf push <APP_NAME> -p game-schedule-pdf-generator-VERSION.jar -b https://github.com/cloudfoundry/java-buildpack.git`
