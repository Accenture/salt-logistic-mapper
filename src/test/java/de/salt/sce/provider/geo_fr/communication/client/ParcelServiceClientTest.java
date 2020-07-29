package de.salt.sce.provider.geo_fr.communication.client;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.scaladsl.model.IllegalUriException;
import de.salt.sce.provider.geo_fr.communication.client.ParcelServiceClient;
import de.salt.sce.provider.geo_fr.communication.client.SCEParcelServiceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.actor.ActorSystem.create;
import static akka.stream.ActorMaterializer.create;
import static java.lang.reflect.Modifier.isPrivate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParcelServiceClientTest {

    @Mock
    private Http client;

    @Mock
    private CompletionStage<HttpResponse> httpResponseCompletionStage;

    @Mock
    private CompletableFuture<HttpResponse> completableFuture;

    @Mock
    private HttpRequest request;

    private final ParcelServiceClient parcelServiceClient = new ParcelServiceClient(
            create(create("Service")),
            5000
    );

    @Test
    public void whenConnectionToRemoteServerIsSuccessful_thenReturnResponse() throws ExecutionException, InterruptedException {
        String httpResponseBody = "body";
        HttpResponse response = HttpResponse
                .create()
                .withEntity(httpResponseBody)
                .withStatus(200);

        when(this.client.singleRequest(request)).thenReturn(this.httpResponseCompletionStage);
        when(this.httpResponseCompletionStage.toCompletableFuture()).thenReturn(this.completableFuture);
        when(this.completableFuture.get()).thenReturn(response);

        SCEParcelServiceResponse parcelServiceResponse = this.parcelServiceClient.send(
                this.request,
                this.client,
                "refId"
        );

        assertThat(parcelServiceResponse.getStatus()).isEqualTo(200);
        assertThat(parcelServiceResponse.getBody()).isEqualTo(httpResponseBody);
    }

    @Test
    public void whenConnectionToRemoteServerThrowsError_thenReturnEmptyResponse() {
        when(this.client.singleRequest(request)).thenThrow(IllegalUriException.apply("", ""));

        assertThat(this.parcelServiceClient.send(
                this.request,
                this.client,
                "refId"
        ).getStatus()).isEqualTo(-1);
    }

    @Test
    public void whenResponseBodyIsEmpty_thenConvertingReturnsEmpty() throws ExecutionException, InterruptedException {
        HttpResponse response = HttpResponse
                .create()
                .withEntity(" ")
                .withStatus(200);

        when(this.client.singleRequest(request)).thenReturn(this.httpResponseCompletionStage);
        when(this.httpResponseCompletionStage.toCompletableFuture()).thenReturn(this.completableFuture);
        when(this.completableFuture.get()).thenReturn(response);

        assertThat(this.parcelServiceClient.send(
                this.request,
                this.client,
                "refId"
        ).getBody()).isBlank();
    }

    @Test
    public void whenResponseBodyIsNull_thenConvertingReturnsEmpty() throws IOException {
        when(this.client.singleRequest(request)).thenReturn(null);

        assertThat(this.parcelServiceClient.send(
                this.request,
                this.client,
                "refId"
        ).getStatus()).isEqualTo(-1);
    }

    @Test
    public void whenResponseStatusCodeBetween300_399_thenConvertingReturnsEmpty() throws ExecutionException, InterruptedException {
        String httpResponseBody = "body";
        HttpResponse response = HttpResponse
                .create()
                .withEntity(httpResponseBody)
                .withStatus(300);

        when(this.client.singleRequest(request)).thenReturn(this.httpResponseCompletionStage);
        when(this.httpResponseCompletionStage.toCompletableFuture()).thenReturn(this.completableFuture);
        when(this.completableFuture.get()).thenReturn(response);

        assertThat(this.parcelServiceClient.send(
                this.request,
                this.client,
                "refId"
        ).getStatus()).isEqualTo(300);
    }

    @Test
    public void whenResponseStatusCodeBetween500_thenConvertingReturnsEmpty() throws ExecutionException, InterruptedException {
        String httpResponseBody = "body";
        HttpResponse response = HttpResponse
                .create()
                .withEntity(httpResponseBody)
                .withStatus(500);

        when(this.client.singleRequest(request)).thenReturn(this.httpResponseCompletionStage);
        when(this.httpResponseCompletionStage.toCompletableFuture()).thenReturn(this.completableFuture);
        when(this.completableFuture.get()).thenReturn(response);

        assertThat(this.parcelServiceClient.send(
                this.request,
                this.client,
                "refId"
        ).getStatus()).isEqualTo(500);
    }

    @Test
    public void whenWeWantToHave100ProcentCoverage_thenCheckIsTheDefaultConstructorPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ParcelServiceClient> constructor = ParcelServiceClient.class.getDeclaredConstructor();

        assertThat(isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
