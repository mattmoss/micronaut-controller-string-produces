package controller.string.produces

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

// In all test cases below, Tomcat and Undertow have the same behaviour as Jetty.
@MicronautTest
class DemoControllerSpec extends Specification {

    @Inject
    @Client("/foobar")
    HttpClient client

    // Netty:    pass
    // Jetty:    fail (Content-Type is text/plain)
    void "request /raw, no accept header, expecting String"() {
        when:
        HttpRequest request = HttpRequest.POST("/raw", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        HttpResponse<String> response = client.toBlocking().exchange(request, String)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body() == "{ \"value\": \"raw\" }"
    }

    // Netty:    pass
    // Jetty:    fail (Content-Type is text/plain)
    void "request /wrapped, no accept header, expecting String"() {
        when:
        HttpRequest request = HttpRequest.POST("/wrapped", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        HttpResponse<String> response = client.toBlocking().exchange(request, String)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body() == "{ \"value\": \"wrapped\" }"
    }

    // Netty:    pass
    // Jetty:    pass
    void "request /string, no accept header, produces text/plain, expecting String"() {
        when:
        HttpRequest request = HttpRequest.POST("/string", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        HttpResponse<String> response = client.toBlocking().exchange(request, String)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.TEXT_PLAIN_TYPE
        response.body() == "{ \"value\": \"string\" }"
    }


    // Expect NOT_ACCEPTABLE as there is no matching route producing text/plain.
    // Netty:    pass
    // Jetty:    fails (HTTP status is 404 NOT_FOUND)
    void "request /raw, accept text/plain, expecting String"() {
        when:
        HttpRequest request = HttpRequest.POST("/raw", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_PLAIN)
        client.toBlocking().exchange(request, String)

        then:
        Throwable t = thrown(HttpClientResponseException)
        t.response.status == HttpStatus.NOT_ACCEPTABLE
    }

    // Expect NOT_ACCEPTABLE as there is no matching route producing text/plain.
    // Netty:    pass
    // Jetty:    fails (HTTP status is 404 NOT_FOUND)
    void "request /wrapped, accept text/plain, expecting String"() {
        when:
        HttpRequest request = HttpRequest.POST("/wrapped", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_PLAIN)
        client.toBlocking().exchange(request, String)

        then:
        Throwable t = thrown(HttpClientResponseException)
        t.response.status == HttpStatus.NOT_ACCEPTABLE
    }

    // Netty:    pass
    // Jetty:    pass
    void "request /string, accept text/plain, produces text/plain, expecting String"() {
        when:
        HttpRequest request = HttpRequest.POST("/string", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_PLAIN)
        HttpResponse<String> response = client.toBlocking().exchange(request, String)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.TEXT_PLAIN_TYPE
        response.body() == "{ \"value\": \"string\" }"
    }

    // Netty:    pass
    // Jetty:    fails (exception thrown b/c 'Error decoding HTTP response body' b/c Content-Type is text/plain)
    void "request /raw, no accept header, expecting Map"() {
        when:
        HttpRequest request = HttpRequest.POST("/raw", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body().get("value") == "raw"
    }

    // Netty:    pass
    // Jetty:    fails (exception thrown b/c 'Error decoding HTTP response body' b/c Content-Type is text/plain)
    void "request /wrapped, no accept header, expecting Map"() {
        when:
        HttpRequest request = HttpRequest.POST("/wrapped", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body().get("value") == "wrapped"
    }

    // Server does as expected, client wrongly expects a Map instead of a String.
    // Netty:    pass
    // Jetty:    pass
    void "request /string, no accept header, produces text/plain, expecting Map"() {
        when:
        HttpRequest request = HttpRequest.POST("/string", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        client.toBlocking().exchange(request, Map)

        then:   'exception b/c client cannot decode plain text into a Map'
        Throwable t = thrown(HttpClientResponseException)
        t.message.contains 'Error decoding HTTP response body'

        and:    'but the response from the server was good'
        t.response.status == HttpStatus.OK
        t.response.contentType.get() == MediaType.TEXT_PLAIN_TYPE
        t.response.getBody(String).get() == "{ \"value\": \"string\" }"
    }

    // Netty:    pass
    // Jetty:    fails (exception thrown b/c 'Error decoding HTTP response body' b/c Content-Type is text/plain)
    void "request /raw, accept application/json, expecting Map"() {
        when:
        HttpRequest request = HttpRequest.POST("/raw", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body().get("value") == "raw"
    }

    // Netty:    pass
    // Jetty:    fails (exception thrown b/c 'Error decoding HTTP response body' b/c Content-Type is text/plain)
    void "request /wrapped, accept application/json, expecting Map"() {
        when:
        HttpRequest request = HttpRequest.POST("/wrapped", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map)

        then:
        noExceptionThrown()
        response.status == HttpStatus.OK
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body().get("value") == "wrapped"
    }

    // Expect NOT_ACCEPTABLE as there is no matching route producing application/json.
    // Netty:    pass
    // Jetty:    fails (HTTP status is 404 NOT_FOUND)
    void "request /string, accept application/json, produces text/plain, expecting Map"() {
        when:
        HttpRequest request = HttpRequest.POST("/string", "")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
        client.toBlocking().exchange(request, Map)

        then:
        Throwable t = thrown(HttpClientResponseException)
        t.response.status == HttpStatus.NOT_ACCEPTABLE
    }

}
