package de.salt.sce.provider.geo_fr.communication.client;

import de.salt.sce.provider.geo_fr.model.ParcelServiceResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.salt.sce.provider.geo_fr.communication.client.ParcelServiceResponseParser.parseResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ParcelServiceResponseParserTest {

    @Test
    public void whenResponseIsValidAndSuccessful_thenCorrectUnmarshal() throws IOException {
        String responseBody = IOUtils.toString(
                this.getClass().getResourceAsStream("Response_Success.json"),
                UTF_8
        );

        ParcelServiceResponse response = parseResponse(responseBody);

        assertThat(response).isNotNull();
        assertThat(response.getContenu()).isNotNull();
        assertThat(response.getContenu().getListSuivis().size()).isEqualTo(12);

        assertThat(response.getContenu().getNoSuivi()).isEqualTo("1GJX9ER2BRPE");
        assertThat(response.getContenu().getDateDepart()).isEqualTo("2019-12-02");

        assertThat(response.getContenu().getListSuivis().get(0).getDateSuivi()).isEqualTo("2019-12-06");
        assertThat(response.getContenu().getListSuivis().get(0).getDateSuiviFrs()).isEqualTo("06/12/2019");
    }
}