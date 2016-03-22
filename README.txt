
PROJECT
--------
A simple Bid tracker application

PREREQUISITES TO BUILD THE PROJECT:
-----------------------------------
Eclipse (ideally to use contained .classpath and .project files)
Maven 3
Java 6

NOTE-  here are currently issues with cobertura and Java 7.
http://mail.openjdk.java.net/pipermail/jdk7-dev/2011-September/002263.html
Since I am using cobertura as code coverage tool here its advisable to build and run the project using Java 6

HIGHLIGHTS:
-----------
Lock Free Implementation of the storage
TDD
Documentation

IF I HAD MORE TIME:
-------------------
Would have put some java docs in the tests
Would have added logging


INSTRUCTIONS:
------------
When using the .classpath and .project files to load the project in Eclipse, please make sure you
have an Eclipse classpath variable M2_REPO pointing to your Maven local repository.

- Open command prompt/terminal, go to phome-test/HEAD and run
    mvn clean install javadoc:javadoc javadoc:test-javadoc cobertura:cobertura

- I've however left a ready built target folder so you can see reports about the project:
-- target/site/apidocs/index.html: API docs index file
-- target/site/testapidocs/index.html: Test API docs index file
-- target/site/cobertura/index.html: Cobertura (test code coverage) index file

- The project layout is in the Maven layout. If not familiar:
-- pom.xml: Build configuration file with dependencies
-- src/main/java: Main java source files
-- src/main/resources: Main resource files e.g. log4j properties file
-- src/test/java: Test java source files


