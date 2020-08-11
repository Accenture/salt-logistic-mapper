package de.salt.sce.mapper.parser;

import de.salt.sce.mapper.model.TrackContract;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageParserTest {

    @Test
    public void whenRecieveCorrectUPSFile_thenParseSuccessful() throws IOException {
        String fileName = "20170516_093419_20160719_141122_ROTH-IFTSTA";

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "ups",
                "/smooks/config-ups.xml",
                "edifact",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(trackContracts.size()).isEqualTo(2);

        assertThat(trackContracts.get(0).getRefId()).isEqualTo("1Z3F4W576807747148");
        assertThat(trackContracts.get(0).getRefType()).isEqualTo("XSITRA");
        assertThat(trackContracts.get(0).getStateId()).isEqualTo("21");
        assertThat(trackContracts.get(0).getTimestamp()).isEqualTo("20160714173746");
        assertThat(trackContracts.get(0).getEdcid()).isEqualTo("ups");
        assertThat(trackContracts.get(0).getStreet()).isEqualTo("21 HOERNLEWEG; E");
        assertThat(trackContracts.get(0).getPcode1()).isEqualTo("73235");
        assertThat(trackContracts.get(0).getCity()).isEqualTo("WEILHEIM");
        assertThat(trackContracts.get(0).getCountryiso()).isEqualTo("DE");

        assertThat(trackContracts.get(1).getRefId()).isEqualTo("1Z562V506807737844");
        assertThat(trackContracts.get(1).getRefType()).isEqualTo("XSITRA");
        assertThat(trackContracts.get(1).getStateId()).isEqualTo("101");
        assertThat(trackContracts.get(1).getTimestamp()).isEqualTo("20160714173339");
        assertThat(trackContracts.get(1).getEdcid()).isEqualTo("ups");
    }

    private byte[] getResource(String resourseName, String encoding) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(resourseName),
                encoding
        ).getBytes(encoding);
    }
}