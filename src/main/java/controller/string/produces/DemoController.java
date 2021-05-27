package controller.string.produces;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Controller("/foobar")
public class DemoController {

    @Post("/raw")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String raw() {
        return json("raw");
    }

    @Post("/wrapped")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Single<MutableHttpResponse<?>> wrapped() {
        return Flowable.fromArray(json("wrapped"))
                .first(json("default"))
                .map(HttpResponse::ok);
    }

    @Post("/string")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String str() {
        return json("string");
    }


    private String json(String v) {
        return "{ \"" + "value" + "\": \"" + v + "\" }";
    }
}
