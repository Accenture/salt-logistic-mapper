package de.salt.sce.mapper.communication.getconfigs;

import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import com.google.gson.JsonSyntaxException;
import com.typesafe.config.Config;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;

import static de.salt.sce.mapper.communication.getconfigs.ConfigResponseParser.parseResponse;
import static de.salt.sce.mapper.communication.getconfigs.ConfigResponseWriter.replaceAndWrite;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Assembly smooks configuration copy functionalities in one place.
 */
public class ConfigCopy {
    private static final Logger log = getLogger(ConfigCopy.class);

    /**
     * Getting smooks files from Microservice and writing in local filesystem.
     *
     * @param httpClient          {@link Http}
     * @param actorMaterializer   {@link ActorMaterializer}
     * @param config              {@link Config}
     * @param microserviceName    String
     * @param rootFolder          String
     * @param requestTimeoutMills int
     */
    public static void copy(Http httpClient, ActorMaterializer actorMaterializer, Config config, String microserviceName, String rootFolder, int requestTimeoutMills) {
        String host = config.getString("host");
        String username = config.getString("username");
        String password = config.getString("password");

        ConfigClient configClient = new ConfigClient(actorMaterializer, requestTimeoutMills);
        Optional<String> result = configClient.send(
                ConfigRequest.buildRequest(host, username, password),
                httpClient,
                host
        );

        try {
            if (result.isPresent()) {
                replaceAndWrite(
                        rootFolder,
                        parseResponse(result.get())
                );
            } else {
                log.error("Could not create smooks config for " + microserviceName);
            }
        } catch (IOException | JsonSyntaxException e) {
            log.error("Could not create smooks config for " + microserviceName);
            e.printStackTrace();
        }
    }
}
