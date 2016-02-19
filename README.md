
SFSU Advising Tool
==================

This tool helps with Senior Advising in the Computer Science Department at the
San Francisco State University. It is written in Java using Servlets running
inside a Jetty application server. The following steps are required to run the
tool:

- Clone the sources from the Github repository:
``
git clone git@github.com:apuder/advising.git
``
- Create a local MySQL database via:
```
mysql -u root -p
CREATE USER 'advisor'@'localhost' IDENTIFIED BY '<mysql-passwd>';
GRANT ALL PRIVILEGES ON * . * TO 'advisor'@'localhost';
FLUSH PRIVILEGES;
```
  Note that 'mysql-passwd' should be replaced with a password.
- Contact ITS for credentials to access the campus Oracle database.
- Create a file called `advisor.properties` (see `advisor.properties_sample`
  for a sample). In that file edit the credentials for the local and the
  campus database.
- Edit the core and elective course list in `course_requirements.json`
- Run via `./gradlew jettyRunWar`
- Visit `http://localhost:8080/advising`

It is recommended to proxy the connection via Apache and use SSL for incoming
traffic.

In order to debug, set the following environemnt variable before running `gradlew`:

``
export GRADLE_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1099"
``

Next use the `JettyDebug` configuration in IntelliJ.

This code is released under the Apache 2 Open Source license. Pull requests are welcome!