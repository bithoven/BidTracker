 Auction Bid Tracker > Requirement
You have been tasked with building part of a simple online auction system which will allow users to bid onitems for sale.
Provide a bid tracker interface and concrete implementation with the following functionality:
Record a user's bid on an item
Get the current winning bid for an item
Get all the bids for an item
Get all the items on which a user has bid
You are not required to implement a GUI (or CLI) or persistent storage. You may use any appropriatelibraries to help, but do not include the jars or class files in your submission.


PREREQUISITES TO BUILD THE PROJECT:
-----------------------------------
Eclipse (ideally to use contained .classpath and .project files)
Maven 3
Java 6


HIGHLIGHTS:
-----------
CAS based Lock Free Implementation

TDD

Datastructure choices

Code documentation

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


