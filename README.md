# Netty vs. Servlet Response Content-Type Issue

## Overview

The `Content-Type` of an HTTP response from Micronaut can be controlled by the `@Produces` annotation or similar
attributes of `@Controller`, `@Get`, `@Post`, etc. However, in the absence of an explicit value, the default will be
`application/json`, as documented in the Micronaut user guide on [Response Content-Type](https://docs.micronaut.io/latest/guide/#producesAnnotation):
_a Micronaut controller produces `application/json` by default_.

> **NOTE:**  The only documented exception is in the user guide section on
> [File Transfers](https://docs.micronaut.io/latest/guide/#transfers), which indicates that _the `Content-Type` header
> of file responses is calculated based on the name of the file._

When Micronaut is using the netty runtime, things appear to work as documented. However, using any of the servlet
runtimes (i.e. jetty, tomcat, or undertow) will generate a response with `Content-Type` of `text/plain` if the response
body is a `String` (or, more specifically, an instance of `CharSequence`).

This project includes several tests, all of which netty passes. If you switch the runtime to one of the servlet
runtimes, most tests will fail. The server is sending back the expected data, but because the `Content-Type` is not
`application/json` but rather `text/plain`, the HTTP client will refuse to parse the text to build a Map, e.g.

## Steps to Reproduce

1. Run the tests using `gradlew test`. The initial runtime is netty and all tests should pass.
1. Edit `build.gradle`. Comment out:

       runtime("netty")

   and uncomment:

       runtime("jetty")
1. Run the tests again using `gradlew test`. Currently, jetty/tomcat/undertow pass only 3 out of 12 tests.
