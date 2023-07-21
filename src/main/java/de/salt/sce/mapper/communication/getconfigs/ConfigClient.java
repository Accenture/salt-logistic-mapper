package de.salt.sce.mapper.communication.getconfigs;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.Materializer;
import org.slf4j.Logger;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Client for getting Smooks Configuration from Microservice.
 */
public class ConfigClient {
    private static final Logger log = getLogger(ConfigClient.class);

    private final Materializer materializer;
    private final int requestTimeoutMills;

    public ConfigClient(Materializer materializer, int requestTimeoutMills) {
        this.materializer = materializer;
        this.requestTimeoutMills = requestTimeoutMills;
    }

    /**
     * Receiving response from Microservice with smooks configurations
     *
     * @param request         {@link HttpRequest}
     * @param client          {@link Http}
     * @param microserviceUrl String
     * @return {@link Optional <String>}
     */
    public Optional<String> send(HttpRequest request, Http client, String microserviceUrl) {
        try {
            HttpResponse response = client.singleRequest(request).toCompletableFuture().get();
            int responseStatusCode = response.status().intValue();
            if (responseStatusCode != 200) {
                return Optional.empty();
            }
            return Optional.of(response
                    .entity()
                    .toStrict(this.requestTimeoutMills, materializer)
                    .toCompletableFuture()
                    .get()
                    .getData()
                    .utf8String()
            );

        } catch (Exception e) {
            log.error("Sending to {} getting exception {} ",
                    microserviceUrl,
                    e.getMessage());

            return Optional.empty();
        }
    }
}
