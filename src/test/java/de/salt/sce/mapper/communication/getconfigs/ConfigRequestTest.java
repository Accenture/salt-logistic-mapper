package de.salt.sce.mapper.communication.getconfigs;

import akka.http.javadsl.model.HttpRequest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static akka.http.javadsl.model.HttpHeader.parse;
import static akka.http.javadsl.model.headers.HttpCredentials.createBasicHttpCredentials;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigRequestTest {

    @Test
    public void whenBuildingRequest_thenRequestContainsAllParameters() throws ExecutionException, InterruptedException {
        String parcelServiceUrl = "https://localhost:1/track/wrack";
        String parcelServiceClientUsername = "username";
        String parcelServiceClientPassword = "password";

        HttpRequest request = ConfigRequest.buildRequest(
                parcelServiceUrl,
                parcelServiceClientUsername,
                parcelServiceClientPassword

        );

        assertThat(request.getHeader("Authorization"))
                .isPresent().get()
                .isEqualTo(parse("Authorization", createBasicHttpCredentials(parcelServiceClientUsername, parcelServiceClientPassword).value()));
        assertThat(request.method().value()).isEqualTo("GET");
        assertThat(request.getUri().toString()).isEqualTo(parcelServiceUrl);
    }
}