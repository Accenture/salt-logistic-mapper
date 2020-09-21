package de.salt.sce.mapper.communication.getconfigs;

import de.salt.sce.modelftp.provider.model.Responses.InternalSmooksFilesResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.salt.sce.mapper.communication.getconfigs.ConfigResponseParser.parseResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigResponseParserTest {

    @Test
    public void whenGivenCorrectSmooksConfigFile_thenParsingToConfigResponseObject() throws IOException {
        String responseJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("ups_response.json"),
                UTF_8
        );

        InternalSmooksFilesResponse configResponse = parseResponse(responseJSon);

        assertThat(configResponse.extResponse().name()).isEqualTo("ups");
        assertThat(configResponse.extResponse().files()).hasSize(3);
        assertThat(configResponse.extResponse().files()[0].fileName()).isEqualTo("config-ups.xml");
        assertThat(configResponse.extResponse().files()[1].fileName()).isEqualTo("mapping-ups.xml");
        assertThat(configResponse.extResponse().files()[2].fileName()).isEqualTo("modelset-definitions-d07b.xml");
    }
}