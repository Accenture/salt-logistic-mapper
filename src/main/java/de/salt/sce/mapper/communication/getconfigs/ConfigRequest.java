package de.salt.sce.mapper.communication.getconfigs;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.headers.HttpCredentials;
import org.slf4j.Logger;

import static akka.http.javadsl.model.headers.HttpCredentials.createBasicHttpCredentials;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Building request with reference Id.
 */
public class ConfigRequest {
    private static final Logger log = getLogger(ConfigRequest.class);

    /**
     * Building Get request to microservice.
     *
     * @param microserviceUrl      String
     * @param microserviceUsername String
     * @param microservicePassword String
     * @return {@link HttpRequest}
     */
    public static HttpRequest buildRequest(String microserviceUrl, String microserviceUsername, String microservicePassword) {
        log.trace("Building smooks configuration request to microservice {}", microserviceUrl);

        return HttpRequest.GET(microserviceUrl)
                .addCredentials(
                        createBasicAuthorisation(microserviceUsername, microservicePassword)
                );
    }

    private static HttpCredentials createBasicAuthorisation(String microserviceUsername, String microservicePassword) {
        return createBasicHttpCredentials(microserviceUsername, microservicePassword);
    }
}
