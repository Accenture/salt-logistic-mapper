package de.salt.sce.provider.geo_fr.communication.client;

import de.salt.sce.provider.model.pojo.UtilObjectResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class ResponseToUtilObjectConverterTest {

    private static final Logger log = getLogger(ResponseToUtilObjectConverterTest.class);

    private ResponseToUtilObjectConverter responseToUtilObjectConverter = new ResponseToUtilObjectConverter();

    @Test
    public void whenResponseIsNotValidJSon_thenConvertingReturnsEmpty() {
        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                "not valid json", "referenceId"
        );

        assertThat(utilObjectResponse).isNotNull();
    }

    @Test
    public void whenResponseHasWrongDatetime_thenConvertingReturnsEmpty() throws IOException {
        String responseBodyJson = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success_WrongDatetimeFormat.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJson, "referenceId"
        );

        assertThat(utilObjectResponse.trackContracts.size()).isOne();
        assertThat(utilObjectResponse.trackErrors.size()).isOne();

        assertThat(utilObjectResponse.trackContracts.get(0).refId()).isEqualTo("5164487167");
        assertThat(utilObjectResponse.trackContracts.get(0).stateId()).isEqualTo("LIVCFM");
        assertThat(utilObjectResponse.trackContracts.get(0).stateTextExt()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).timestamp()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).edcid()).isEqualTo("GEO_FR");

        assertThat(utilObjectResponse.trackErrors.get(0).errorCode()).isEqualTo("Warning");
        assertThat(utilObjectResponse.trackErrors.get(0).hash()).isEqualTo("");
        assertThat(utilObjectResponse.trackErrors.get(0).refIds()).isEqualTo("5164487167");
        assertThat(utilObjectResponse.trackErrors.get(0).message()).startsWith("Incoming date and time");

    }

    @Test
    public void whenResponseIsSuccess_thenConvertingReturnsCorrectObject() throws IOException {
        String responseBodyJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJSon, "referenceId"
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
    public void whenResponseIsSuccessWithErrorCode_thenConvertingReturnsCorrectObject() throws IOException {
        String responseBodyJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success_WithErrorCode.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJSon, "referenceId"
        );

        assertThat(utilObjectResponse.trackErrors.size()).isOne();
        assertThat(utilObjectResponse.trackContracts.size()).isZero();

        assertThat(utilObjectResponse.trackErrors.get(0).errorCode()).isEqualTo("ErrorCode");
        assertThat(utilObjectResponse.trackErrors.get(0).hash()).isEqualTo("");
        assertThat(utilObjectResponse.trackErrors.get(0).refIds()).isEqualTo("referenceId");
        assertThat(utilObjectResponse.trackErrors.get(0).message()).isEqualTo("ErrorCodeDesciption");
    }

    @Test
    public void whenResponseIsSuccessWithoutDateBlocks_thenConvertingReturnsCorrectObject() throws IOException {
        String responseBodyJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success_WithoutDateBlocks.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJSon, "referenceId"
        );

        assertThat(utilObjectResponse.trackErrors.size()).isZero();
        assertThat(utilObjectResponse.trackContracts.size()).isOne();

        assertThat(utilObjectResponse.trackContracts.get(0).refId()).isEqualTo("referenceId");
        assertThat(utilObjectResponse.trackContracts.get(0).stateId()).isEqualTo("SOLANN");
        assertThat(utilObjectResponse.trackContracts.get(0).stateTextExt()).isEqualTo("There is no shipment status for refId: referenceId. Empty list of shipment status.");
    }

    @Test
    public void whenResponseIsSuccessWithNullElements_thenConvertingReturnsCorrectObject() throws IOException {
        String responseBodyJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success_elementsNull.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJSon, "referenceId"
        );

        assertThat(utilObjectResponse.trackContracts.size()).isOne();

        assertThat(utilObjectResponse.trackContracts.get(0).refId()).isEqualTo("5164487167");
        assertThat(utilObjectResponse.trackContracts.get(0).stateId()).isEqualTo("LIVCFM");
        assertThat(utilObjectResponse.trackContracts.get(0).mail()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).phoneNo()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).faxNo()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).countryiso()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).name1()).isEqualTo("");
        assertThat(utilObjectResponse.trackContracts.get(0).timestamp()).isEqualTo("");

        assertThat(utilObjectResponse.trackErrors.size()).isOne();
        log.debug("Error Entry: {}, {}, {} " , utilObjectResponse.trackErrors.get(0).refIds() , utilObjectResponse.trackErrors.get(0).errorCode(), utilObjectResponse.trackErrors.get(0).message());
        assertThat(utilObjectResponse.trackErrors.get(0).refIds()).isEqualTo("5164487167");
        assertThat(utilObjectResponse.trackErrors.get(0).errorCode()).isEqualTo("Warning");
        assertThat(utilObjectResponse.trackErrors.get(0).message()).startsWith("Incoming date and time [nullnull] does not match expected format ");


    }

    @Test
    public void whenResponseIsSuccessWithTrackingStateNull_thenConvertingReturnsCorrectObject() throws IOException {
        String responseBodyJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success_trackingStateNull.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJSon, "referenceId"
        );

        assertThat(utilObjectResponse.trackErrors.size()).isZero();
        assertThat(utilObjectResponse.trackContracts.size()).isOne();

        assertThat(utilObjectResponse.trackContracts.get(0).refId()).isEqualTo("referenceId");
        assertThat(utilObjectResponse.trackContracts.get(0).stateId()).isEqualTo("SOLANN");
        assertThat(utilObjectResponse.trackContracts.get(0).stateTextExt()).isEqualTo("Invalid file format: Shipment status for refId: [referenceId] is null");
    }

    @Test
    public void whenResponseIsSuccessForNonExistingRefID_thenConvertingReturnsCorrectObject() throws IOException {
        String responseBodyJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success_NonExistingRefID.json"),
                UTF_8
        );

        UtilObjectResponse utilObjectResponse = this.responseToUtilObjectConverter.convert(
                responseBodyJSon, "referenceId"
        );

        assertThat(utilObjectResponse.trackErrors.size()).isZero();
        assertThat(utilObjectResponse.trackContracts.size()).isOne();

        assertThat(utilObjectResponse.trackContracts.get(0).refId()).isEqualTo("referenceId");
        assertThat(utilObjectResponse.trackContracts.get(0).stateId()).isEqualTo("SOLANN");
        assertThat(utilObjectResponse.trackContracts.get(0).stateTextExt()).isEqualTo("There is no shipment status for refId: referenceId. Empty list of shipment status.");
    }
}