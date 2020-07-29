package de.salt.sce.provider.geo_fr;

import akka.actor.ActorSystem;
import de.salt.sce.provider.geo_fr.communication.client.ParcelServiceClient;
import de.salt.sce.provider.geo_fr.communication.client.ParcelServiceRequestBuilder;
import de.salt.sce.provider.geo_fr.communication.client.ResponseToUtilObjectConverter;
import de.salt.sce.provider.geo_fr.communication.client.SCEParcelServiceResponse;
import de.salt.sce.provider.model.communication.model.RequestData;
import de.salt.sce.provider.model.pojo.UtilObjectResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;

import static de.salt.sce.provider.geo_fr.WebServiceClientTrack.buildUtilObjectResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebServiceClientTrackTest {

    @Mock
    ParcelServiceClient parcelServiceClient;

    private final ActorSystem actorSystem = ActorSystem.create("System");
    private final ResponseToUtilObjectConverter responseToUtilObjectConverter = new ResponseToUtilObjectConverter();
    private final ParcelServiceRequestBuilder parcelServiceRequestBuilder = new ParcelServiceRequestBuilder(RequestData.apply(
            "0123456789",
            "https://www.google.de/",
            8888,
            "id_dispatcher",
            "pw_dispatcher",
            "",
            "X",
            "",
            "UTF-8",
            // 3 Fedex mock tracking numbers: 449044304137821,149331877648230,122816215025810
            "504B0304140000000800E6799B505EAFBCE3260000002F000000070000006D795F6461746105C1490100200C0330433C7AB115FFC6489287C408BD150FF36C76775219875239E2856E890F504B01021300140000000800E6799B505EAFBCE3260000002F0000000700000000000000000000000000000000006D795F64617461504B05060000000001000100350000004B0000000000"
    ));

    @Test
    public void whenParcelServiceReturnsCorrectXML_thenCreateCorrectUtilObjectResponse() throws IOException {
        String responseBody = IOUtils.toString(
                this.getClass().getResourceAsStream("communication/client/Response_Success.json"),
                UTF_8
        );

        when(parcelServiceClient.send(any(), any(), anyString())).thenReturn(new SCEParcelServiceResponse(200, responseBody));

        UtilObjectResponse utilObjectResponse = buildUtilObjectResponse(
                responseToUtilObjectConverter,
                parcelServiceClient,
                parcelServiceRequestBuilder,
                actorSystem,
                singletonList("refId")
        );

        assertThat(utilObjectResponse.trackContracts.size()).isEqualTo(12);
        assertThat(utilObjectResponse.trackErrors.size()).isZero();

        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.refId()).isEqualTo("5164487167"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.city()).isEqualTo("GEMENOS"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.pcode1()).isEqualTo("13420"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.street()).isEqualTo("ZA LE PETIT VERSAILLES"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.countryiso()).isEqualTo("FR"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.faxNo()).isEqualTo(""));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.phoneNo()).isEqualTo("331234567890"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.district()).isEqualTo("13"));
        utilObjectResponse.trackContracts.forEach(contract -> assertThat(contract.mail()).isEqualTo("mail@alliance-st-bois.fr"));

        assertThat(utilObjectResponse.trackContracts.get(0).stateId()).isEqualTo("LIVCFM");
        assertThat(utilObjectResponse.trackContracts.get(0).timestamp()).isEqualTo("20191206143100");

        assertThat(utilObjectResponse.trackContracts.get(1).stateId()).isEqualTo("MLVCFM");
        assertThat(utilObjectResponse.trackContracts.get(1).timestamp()).isEqualTo("20191206133224");

        assertThat(utilObjectResponse.trackContracts.get(2).stateId()).isEqualTo("RSTLDF");
        assertThat(utilObjectResponse.trackContracts.get(2).timestamp()).isEqualTo("20191206091824");

        assertThat(utilObjectResponse.trackContracts.get(3).stateId()).isEqualTo("RSTSOU");
        assertThat(utilObjectResponse.trackContracts.get(3).timestamp()).isEqualTo("20191204105906");

        assertThat(utilObjectResponse.trackContracts.get(4).stateId()).isEqualTo("RENCAD");
        assertThat(utilObjectResponse.trackContracts.get(4).timestamp()).isEqualTo("20191203093000");

        assertThat(utilObjectResponse.trackContracts.get(5).stateId()).isEqualTo("MLVCFM");
        assertThat(utilObjectResponse.trackContracts.get(5).timestamp()).isEqualTo("20191203084018");

        assertThat(utilObjectResponse.trackContracts.get(6).stateId()).isEqualTo("RSTAPM");
        assertThat(utilObjectResponse.trackContracts.get(6).timestamp()).isEqualTo("20191203080945");

        assertThat(utilObjectResponse.trackContracts.get(7).stateId()).isEqualTo("AARCFM");
        assertThat(utilObjectResponse.trackContracts.get(7).timestamp()).isEqualTo("20191203071501");

        assertThat(utilObjectResponse.trackContracts.get(8).stateId()).isEqualTo("AARTAR");
        assertThat(utilObjectResponse.trackContracts.get(8).timestamp()).isEqualTo("20191203065230");

        assertThat(utilObjectResponse.trackContracts.get(9).stateId()).isEqualTo("EXPCFM");
        assertThat(utilObjectResponse.trackContracts.get(9).timestamp()).isEqualTo("20191203010216");

        assertThat(utilObjectResponse.trackContracts.get(10).stateId()).isEqualTo("AARCFM");
        assertThat(utilObjectResponse.trackContracts.get(10).timestamp()).isEqualTo("20191202230954");

        assertThat(utilObjectResponse.trackContracts.get(11).stateId()).isEqualTo("EXPCFM");
        assertThat(utilObjectResponse.trackContracts.get(11).timestamp()).isEqualTo("20191202191857");
    }

    @Test
    public void whenParcelServiceReturnsNoObject_thenCreateCorrectErrorUtilObjectResponse() {

        when(parcelServiceClient.send(any(), any(), anyString())).thenReturn(new SCEParcelServiceResponse(-1, ""));

        UtilObjectResponse utilObjectResponse = buildUtilObjectResponse(
                responseToUtilObjectConverter,
                parcelServiceClient,
                parcelServiceRequestBuilder,
                actorSystem,
                Collections.singletonList("refId")
        );

        assertThat(utilObjectResponse.trackErrors.size()).isOne();
        assertThat(utilObjectResponse.trackContracts.size()).isZero();

        assertThat(utilObjectResponse.trackErrors.get(0).hash()).isEqualTo("");
        assertThat(utilObjectResponse.trackErrors.get(0).refIds()).isEqualTo("refId");
        assertThat(utilObjectResponse.trackErrors.get(0).errorCode()).isEqualTo("Error");
        assertThat(utilObjectResponse.trackErrors.get(0).message().trim()).isEqualTo("Problems with sending request to Geodis server.");
    }
}