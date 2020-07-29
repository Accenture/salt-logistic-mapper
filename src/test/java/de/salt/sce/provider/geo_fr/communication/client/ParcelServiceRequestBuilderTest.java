package de.salt.sce.provider.geo_fr.communication.client;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import de.salt.sce.provider.geo_fr.util.SpecHelper;
import de.salt.sce.provider.model.communication.model.RequestData;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import static akka.stream.ActorMaterializer.create;
import static java.lang.reflect.Modifier.isPrivate;
import static org.assertj.core.api.Assertions.assertThat;

public class ParcelServiceRequestBuilderTest {

    @Test
    public void whenCreateRequestSuccess_thenRequestContainsAllParameters() throws ExecutionException, InterruptedException {
        String url = "url";
        String service = "service";
        String referenceId = "referenceId";
        String expectedBody = String.format("{\"refUniExp\":\"%s\"}", referenceId);
        RequestData requestData = SpecHelper.buildRequestData();
        ParcelServiceRequestBuilder parcelServiceRequestBuilder = new ParcelServiceRequestBuilder(requestData);

        HttpRequest request = parcelServiceRequestBuilder.buildParcelServiceRequest(referenceId);

        assertThat(request.method().name()).isEqualTo("POST");
        assertThat(request.getUri().toString()).isEqualTo(requestData.host() + "/" + requestData.getCustomProperty("service"));
        assertThat(request.getHeader("X-GEODIS-Service")).isPresent();

        assertThat(
                request.entity().toStrict(1000, create(ActorSystem.create("Sys"))).toCompletableFuture().get().getData().utf8String()
        ).isEqualTo(expectedBody);
    }

    @Test
    public void whenWeWantToHave100ProcentCoverage_thenCheckIsTheDefaultConstructorPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ParcelServiceRequestBuilder> constructor = ParcelServiceRequestBuilder.class.getDeclaredConstructor();

        assertThat(isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}