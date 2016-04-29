# Cups4j Rest Server

A simple rest server that wraps the [cups4j](http://www.cups4j.org/) in a REST
API.

The main objetive is to provide a simple REST API to connect to a CUPS server
inside a docker container. This project needs to run in the same container as
the CUPS Server.

The credential's to the basic authorization for this wrapper are located in
`/tomcat.credentials`, but that can be changed in the Config.java file. The
format of the file is `USER:PASS` like in the Basic header.
