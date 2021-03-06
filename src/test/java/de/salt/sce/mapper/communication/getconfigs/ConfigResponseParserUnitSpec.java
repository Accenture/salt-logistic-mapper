package de.salt.sce.mapper.communication.getconfigs;

import de.salt.sce.mapper.communication.getconfigs.model.ConfigResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.salt.sce.mapper.communication.getconfigs.ConfigResponseParser.parseResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigResponseParserUnitSpec {

    @Test
    public void whenGivenCorrectSmooksConfigFile_thenParsingToConfigResponseObject() throws IOException {
        String responseJSon = IOUtils.toString(
                this.getClass().getResourceAsStream("ups_response.json"),
                UTF_8
        );

        ConfigResponse configResponse = parseResponse(responseJSon);

        assertThat(configResponse).isNotNull();
        assertThat(configResponse.getName()).isEqualTo("ups");
        assertThat(configResponse.getFiles()).hasSize(3);
        assertThat(configResponse.getFiles().get(0).getFileName()).isEqualTo("config-ups.xml");
        assertThat(configResponse.getFiles().get(1).getFileName()).isEqualTo("mapping-ups.xml");
        assertThat(configResponse.getFiles().get(2).getFileName()).isEqualTo("modelset-definitions-d07b.xml");
    }
}