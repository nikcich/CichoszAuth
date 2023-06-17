## If anyone sees this im sorry this is probably terrible practice but it works for my personal projects.

# Basic Auth Service So I Can Stop Rewriting Over And Over

### Run with glassfish or tomcat etc.

### Exposed to http://hostname:port/CichoszAuth/auth/service

# Configuration
### src/main/resources/config.properties

### Set up database config and cleanup configs for the "session" (not really a session but whatever) cache.
### Only supports MariaDB as currently configured.


# Configuration Part 2

### src/main/webapp/WEB-INF/web.xml
### the route portion following the package name i.e. /CichoszAuth/<This Portion>/service 
### Can be changed in the URL-patern XML element


### Should be relatively easy to add whatever other features needed to work with authentication
### Use the "session" token for auth on API requests
### Can create a endpoint that accepts a "session" token and verifies it
### reverse proxy this or something, make requests from other app components then to use? Idk. Could be interesting
### https://github.com/nikcich/ExpressProxyMiddleware



# Database Configuration

### This requires a MariaDB database to be set up.
### Not sure im even using the uid or data columns but they are there.. Likely can be removed.

### Table: user
### Columns:
### id int(11) AI PK 
### uid varchar(255) 
### data longtext 
### username varchar(255) 
### password varchar(255)


# Usage/Interaction

## Login Can be handled something like this

```js
const raw = JSON.stringify({
    "username": username,
    "password": pw
});

const requestOptions = {
    method: 'POST',
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
    },
    body: raw,
};

fetch(API_CONFIG.base_url + "CichoszAuth/auth/service/login", requestOptions)
    .then(response => {
        status = response.status;
        return response.text();
    })
    .then(response => {
        const res = JSON.parse(response);
        if (status != 200) {
            setErrorMessage(res["error"]);
            return;
        }
        const session = res["session"];
        setSession(session);
    })
    .catch(error => console.log('error', error));
```

## SignUp Can be handled something like this

```js
const raw = JSON.stringify({
    "username": username,
    "password": pw
});

const requestOptions = {
    method: 'POST',
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
    },
    body: raw,
};

fetch(API_CONFIG.base_url + "CichoszAuth/auth/service/signup", requestOptions)
    .then(response => {
        status = response.status;
        return response.text();
    })
    .then(response => {
        const res = JSON.parse(response);
        if (status != 200) {
            setErrorMessage(res["error"]);
            return;
        }
        const session = res["session"];
        setSession(session);
    })
    .catch(error => console.log('error', error));

```

## Future API Calls can then use the session token it was given something like this:

```js
const requestOptions = {
    method: 'GET',
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        'Session': session
    }
};

fetch(API_CONFIG.base_url + "CichoszAuth/auth/service/<ENDPOINT>", requestOptions)
    .then(response => {
        status = response.status;
        return response.text();
    })
    .then(response => {
        if (status != 200) {
            // Do error handling here
            return;
        }

        // Do something with response
     })
    .catch(error => console.log('error', error));
```


# TODO: Write example for deploying with glassfish?

## JDK 8 and Glassfish 5
## https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html
## https://download.oracle.com/glassfish/5.0/release/index.html

## http://hostname:4848/

## Username: admin
## password: admin

# Glassfish commands

### sudo systemctl daemon-reload
### ExecStart = /usr/bin/java -jar /opt/glassfish5/glassfish/lib/client/appserver-cli.jar start-domain <domainname>
### ExecStop = /usr/bin/java -jar /opt/glassfish5/glassfish/lib/client/appserver-cli.jar stop-domain <domainname>
### ExecReload = /usr/bin/java -jar /opt/glassfish5/glassfish/lib/client/appserver-cli.jar restart-domain <domainname>

### Some reason it doesnt like to restart or stop might need to just kill the process
### find from ps -ef | grep glassfish

### /opt/glassfish5/bin/asadmin --port 4848 change-admin-password
### /opt/glassfish5/bin/asadmin enable-secure-admin Idk why this breaks it so cant access admin panel remotely..
### Just forward port and use like that so u can log in. Otherwise doesnt allow remote...

# Deployment of WAR

### Login to the admin panel at the link above.
### Navigate to the applications section
### Click deploy and choose the WAR file to deploy.