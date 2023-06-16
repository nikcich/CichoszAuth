# Basic Auth Service So I Can Stop Rewriting Over And Over

## Run with glassfish or tomcat etc.

## Exposed to http://hostname:port/CichoszAuth/auth/service

# Configuration
## src/main/resources/config.properties

## Set up database config and cleanup configs for the "session" (not really a session but whatever) cache.
## Only supports MariaDB as currently configured.


# Configuration Part 2

## src/main/webapp/WEB-INF/web.xml
## the route portion following the package name i.e. /CichoszAuth/<This Portion>/service 
## Can be changed in the URL-patern XML element