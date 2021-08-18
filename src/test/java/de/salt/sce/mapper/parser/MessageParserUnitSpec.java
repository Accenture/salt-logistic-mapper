package de.salt.sce.mapper.parser;

import de.salt.sce.mapper.exception.ParserFailedException;
import de.salt.sce.mapper.util.ObjectSerializer;
import de.salt.sce.model.csv.PaketCSV;
import de.salt.sce.model.edifact.Transport;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.salt.sce.mapper.util.ObjectSerializer.deserialize;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageParserUnitSpec {

    private final MessageParser messageParser = new MessageParser();
    private final String appHomePath = new File(".").getCanonicalPath();

    public MessageParserUnitSpec() throws IOException {
    }

    @Test
    @DisplayName("EMO_DE testing.")
    public void whenRecieveCorrectEmoDeFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "emo_de/20201103_121247_EMONS_STATUS_20201103_115148_000000815188.txt";

        Optional<String> encodedString = messageParser.parseFile(
                "emo_de",
                appHomePath+"/src/test/resources/smooks/emo_de/config-emons.xml",
                "edifact",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        Transport transport = (Transport) deserialize(decodeBase64(encodedString.get()));

        assertThat(transport.getShipments()).hasSize(1);
        assertThat(transport.getShipments().get(0).getPakets()).hasSize(1);
    }

    @Test
    @DisplayName("DHF_DE testing.")
    public void whenRecieveCorrectDhfDeFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "dhf_de/20201103_122230_UVEX_IFTSTA005951.txt";

        Optional<String> encodedString = messageParser.parseFile(
                "dhf_de",
                appHomePath+"/src/test/resources/smooks/dhf_de/config-dhlf.xml",
                "edifact",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        Transport transport = (Transport) deserialize(decodeBase64(encodedString.get()));

        assertThat(transport.getShipments()).hasSize(1);
        assertThat(transport.getShipments().get(0).getPakets()).hasSize(1);
    }


    @Test
    @DisplayName("Testing unknown message type.")
    public void whenRecieveUnknownFileType_thenReturnsEmptyString() throws IOException, ParserFailedException {
        String fileName = "ups/20170516_093419_20160719_141122_ROTH-IFTSTA";

        Optional<String> encodedString = messageParser.parseFile(
                "ups",
                appHomePath+"/src/test/resources/smooks/ups/config-ups.xml",
                "unknown",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isNotPresent();
    }

    @Test
    @DisplayName("UPS testing 1.")
    public void whenRecieveCorrectUPSFile_thenParseSuccessful_1() throws IOException, ParserFailedException {
        String fileName = "ups/20170516_093419_20160719_141122_ROTH-IFTSTA";

        Optional<String> encodedString = messageParser.parseFile(
                "ups",
                appHomePath+"/src/test/resources/smooks/ups/config-ups.xml",
                "edifact",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        Transport transport = (Transport) deserialize(decodeBase64(encodedString.get()));

        assertThat(transport.getShipments()).hasSize(1);
        assertThat(transport.getShipments().get(0).getPakets()).hasSize(2);
    }

    @Test
    @DisplayName("UPS testing 2. Just an another file.")
    public void whenRecieveCorrectUPSFile_thenParseSuccessful_2() throws IOException, ParserFailedException {
        String fileName = "ups/20160121_181708_ROTH-IFTSTA.266.txt";

        Optional<String> encodedString = messageParser.parseFile(
                "ups",
                appHomePath+"/src/test/resources/smooks/ups/config-ups.xml",
                "edifact",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        Transport transport = (Transport) deserialize(decodeBase64(encodedString.get()));

        assertThat(transport.getShipments()).hasSize(1);
        assertThat(transport.getShipments().get(0).getPakets()).hasSize(1);
    }

    @Test
    @DisplayName("UPS testing exception.")
    public void whenRecieveCorrectButNotUPSFile_thenThrowsException() {
        String fileName = "ups/IIFTSTA_Hellmann_Rothenberger-2015-03-10-07-39-42-002.dat.SmooksException";

        assertThrows(ParserFailedException.class,
                () -> {
                    messageParser.parseFile(
                            "ups",
                            appHomePath+"/src/test/resources/smooks/ups/config-ups.xml",
                            "edifact",
                            fileName,
                            getResource(fileName, "windows-1252")
                    );
                });
    }

    @Test
    @DisplayName("DPD testing.")
    public void whenRecieveCorrectDPDFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "dpd/20160201_170112_STATUSDATA_KD2748208P_D20160201T021335";

        Optional<String> encodedString = messageParser.parseFile(
                "dpd",
                appHomePath+"/src/test/resources/smooks/dpd/config-dpd.xml",
                "csv",
                fileName,
                getResource(fileName, "UTF-8")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        List<PaketCSV> paketCSVs = (ArrayList<PaketCSV>) deserialize(decodeBase64(encodedString.get()));

        assertThat(paketCSVs).hasSize(85);
    }

    @Test
    @DisplayName("Dachser testing.")
    public void whenRecieveCorrectDachserFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "dachser/Beispiel_EDIFACT_IFTSTA_D01C.txt";

        Optional<String> encodedString = messageParser.parseFile(
                "dachser",
                appHomePath+"/src/test/resources/smooks/dachser/config-dachser.xml",
                "edifact",
                fileName,
                getResource(fileName, "UTF-8")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        Transport transport = (Transport) deserialize(decodeBase64(encodedString.get()));

        assertThat(transport.getShipments()).hasSize(1);
        assertThat(transport.getShipments().get(0).getPakets()).hasSize(9);
    }

    @Test
    @DisplayName("AMM testing.")
    public void whenRecieveCorrectAMMFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "amm/P0815-STAT_IFTSTA-4.txt";

        Optional<String> encodedString = messageParser.parseFile(
                "amm",
                appHomePath+"/src/test/resources/smooks/amm/config-amm.xml",
                "edifact",
                fileName,
                getResource(fileName, "UTF-8")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        Transport transport = (Transport) deserialize(decodeBase64(encodedString.get()));

        assertThat(transport.getShipments()).hasSize(1);
        assertThat(transport.getShipments().get(0).getPakets()).hasSize(1);
    }

    @Test
    @DisplayName("TOF testing.")
    public void whenRecieveCorrectTOFFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "tof/93449322.ROTHENBE.20140408.AZMX.39471758.11171400.CSV";

        Optional<String> encodedString = messageParser.parseFile(
                "tof",
                appHomePath+"/src/test/resources/smooks/tof/config-tof.xml",
                "csv",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        List<PaketCSV> paketCSVs = (ArrayList<PaketCSV>) deserialize(decodeBase64(encodedString.get()));

        assertThat(paketCSVs).hasSize(40);
    }

    @Test
    @DisplayName("GLS_DE testing.")
    public void whenRecieveCorrectGlsDeFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "gls_de/20200923.133501.pakstat.018";

        Optional<String> encodedString = messageParser.parseFile(
                "gls_de",
                appHomePath+"/src/test/resources/smooks/gls_de/config-gls.xml",
                "csv",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        List<PaketCSV> paketCSVs = (ArrayList<PaketCSV>) deserialize(decodeBase64(encodedString.get()));

        assertThat(paketCSVs).hasSize(10);
    }

    @Test
    @DisplayName("GLS_AT testing.")
    public void whenRecieveCorrectGlsAtFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "gls_at/20201103_122541_pakstat";

        Optional<String> encodedString = messageParser.parseFile(
                "gls_at",
                appHomePath+"/src/test/resources/smooks/gls_at/config-gls.xml",
                "csv",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        List<PaketCSV> paketCSVs = (ArrayList<PaketCSV>) deserialize(decodeBase64(encodedString.get()));

        assertThat(paketCSVs).hasSize(839);
    }

    @Test
    @DisplayName("DHL_DE testing.")
    public void whenRecieveCorrectDhlDeFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "dhl_de/20201103_080624_5023422208_REPSRD_StandardberichtAlpina_B_0152924_20201103070015.txt";

        Optional<String> encodedString = messageParser.parseFile(
                "dhl_de",
                appHomePath+"/src/test/resources/smooks/dhl_de/config-dhl.xml",
                "csv",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(encodedString).isPresent();

        @SuppressWarnings("unchecked")
        List<PaketCSV> paketCSVs = (ArrayList<PaketCSV>) deserialize(decodeBase64(encodedString.get()));

        assertThat(paketCSVs).hasSize(178);
    }

    private byte[] getResource(String resourseName, String encoding) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(resourseName),
                encoding
        ).getBytes(encoding);
    }
}
