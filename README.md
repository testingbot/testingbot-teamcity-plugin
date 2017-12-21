TestingBot TeamCity Plugin
=============================

This plugin allows you to integrate TestingBot with TeamCity.
Features of this plugin:

*    Automate the setup and tear down of TestingBot Tunnel, which allows you to run automated tests on internal websites.
*    Integrate TestingBot videos, screenshots and logs within the TeamCity build output.


Installation
====

[Download](https://testingbot.com/downloads/teamcity-plugin.zip) the plugin zip file and copy it into your ~/.BuildServer/plugins directory

For more information, please see https://testingbot.com/support/other/teamcity

Usage
===

The plugin provides a 'TestingBot Build Feature' which can be added to a TeamCity build.

Enter your TestingBot key and secret in the plugin's settings overview.

In order to integrate your TestingBot tests with the TeamCity build, you will need to include the following output for every test:

    TestingBotSessionID=SESSION_ID

where SESSION_ID is the webdriver session id.

Release process:
===========

Prepare release and perform:

```
mvn release:clean release:prepare
mvn release:perform
```

To create a build:

```
mvn package
```