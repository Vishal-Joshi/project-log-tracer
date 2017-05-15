# project-log-tracer
This application is intended to read log traces, analyze them and prepare traces to be able to decipher the flow of service calls. The output will JSON

# Building locally
`mvn clean install`

# Languages and frameworks
* Production code - Java 8
* Tests - Groovy
* API Framework - Spring boot
* Test framework - Spock

# How to run the application
`mvn spring-boot:run`

# Input and output base path
'base.path' property in application.properties describe the directory from where the log file 
will be picked. 
The currently value for it is **/tmp/logs/**. So, please place the log files there.
                              
'base.path.trace.json.output' property in application.properties describe the directory where the output trace file 
will be generated.                        
The currently value for it is **/tmp/tracer-json-output/**. So, please place the log files there.

# Sample request
`curl -X GET 'http://localhost:9002/logtracerapi/readlogs?traceinputlogfilename=medium-log.txt&tracesoutputfilename=medium-traces9.txt'`
