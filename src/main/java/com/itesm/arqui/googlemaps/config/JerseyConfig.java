package com.itesm.arqui.googlemaps.config;


import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 *
 * @author mklfarha Jersey configuration to load endpoints.
 */
@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(JacksonJaxbJsonProvider.class);
        packages("com.itesm.arqui.googlemaps.endpoint");
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }
}