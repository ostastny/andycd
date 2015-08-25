package cz.ostastny.andycd;

import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {
    public Application() {
        register(new ApplicationBinder());
        register(new HbAwareObjectMapper());
        packages(true, new String[]{"io.swagger.jaxrs.listing", "cz.ostastny.andycd.resources"});
    }
}