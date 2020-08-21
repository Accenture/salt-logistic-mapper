package de.salt.sce.mapper.parser;

import de.salt.sce.mapper.exception.ParserFailedException;
import de.salt.sce.mapper.model.TrackContract;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageParserTest {

    @Test
    @DisplayName("UPS testing 1.")
    public void whenRecieveCorrectUPSFile_thenParseSuccessful_1() throws IOException, ParserFailedException {
        String fileName = "ups/20170516_093419_20160719_141122_ROTH-IFTSTA";

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "ups",
                "classpath:/smooks/ups/config-ups.xml",
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

    @Test
    @DisplayName("UPS testing 2. Just an another file.")
    public void whenRecieveCorrectUPSFile_thenParseSuccessful_2() throws IOException, ParserFailedException {
        String fileName = "ups/20160121_181708_ROTH-IFTSTA.266.txt";

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "ups",
                "classpath:/smooks/ups/config-ups.xml",
                "edifact",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(trackContracts.size()).isOne();

        assertThat(trackContracts.get(0).getRefId()).isEqualTo("1Z562V506807051147");
        assertThat(trackContracts.get(0).getRefType()).isEqualTo("XSITRA");
        assertThat(trackContracts.get(0).getStateId()).isEqualTo("21");
        assertThat(trackContracts.get(0).getTimestamp()).isEqualTo("20160121145418");
        assertThat(trackContracts.get(0).getEdcid()).isEqualTo("ups");
        assertThat(trackContracts.get(0).getStreet()).isEqualTo("18 SATRUPER STR; E");
        assertThat(trackContracts.get(0).getPcode1()).isEqualTo("24860");
        assertThat(trackContracts.get(0).getCity()).isEqualTo("BOEKLUND");
        assertThat(trackContracts.get(0).getCountryiso()).isEqualTo("DE");
    }

    @Test
    @DisplayName("UPS testing exception.")
    public void whenRecieveCorrectButNotUPSFile_thenThrowsException() {
        String fileName = "ups/IIFTSTA_Hellmann_Rothenberger-2015-03-10-07-39-42-002.dat.SmooksException";

        MessageParser messageParser = new MessageParser();

        assertThrows(ParserFailedException.class,
                ()->{
                    messageParser.parseFile(
                            "ups",
                            "classpath:/smooks/ups/config-ups.xml",
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

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "dpd",
                "classpath:/smooks/dpd/config-dpd.xml",
                "csv",
                fileName,
                getResource(fileName, "UTF-8")
        );

        assertThat(trackContracts.size()).isEqualTo(85);

        assertThat(trackContracts.get(0).getRefId()).isEqualTo("09445744184617");
        assertThat(trackContracts.get(0).getRefType()).isEqualTo("XSITRA");
        assertThat(trackContracts.get(0).getStateId()).isEqualTo("18");
        assertThat(trackContracts.get(0).getStateTextExt()).isEqualTo("Zusatzinformation");
        assertThat(trackContracts.get(0).getTimestamp()).isEqualTo("20160130023900");
        assertThat(trackContracts.get(0).getEdcid()).isEqualTo("dpd");


        assertThat(trackContracts.get(84).getRefId()).isEqualTo("09445744184916");
        assertThat(trackContracts.get(84).getRefType()).isEqualTo("XSITRA");
        assertThat(trackContracts.get(84).getStateId()).isEqualTo("02");
        assertThat(trackContracts.get(84).getStateTextExt()).isEqualTo("Eingang");
        assertThat(trackContracts.get(84).getTimestamp()).isEqualTo("20160130054905");
        assertThat(trackContracts.get(84).getEdcid()).isEqualTo("dpd");
    }

    @Test
    @DisplayName("Dachser testing.")
    public void whenRecieveCorrectDachserFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "dachser/Beispiel_EDIFACT_IFTSTA_D01C.txt";

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "dachser",
                "classpath:/smooks/dachser/config-dachser.xml",
                "edifact",
                fileName,
                getResource(fileName, "UTF-8")
        );

        assertThat(trackContracts).hasSize(9);

        assertThat(trackContracts.get(0).getRefId()).isEqualTo("2006830538");
        assertThat(trackContracts.get(0).getRefType()).isEqualTo("XSITRA");
        assertThat(trackContracts.get(0).getStateId()).isEqualTo("45");
        assertThat(trackContracts.get(0).getTimestamp()).isEqualTo("20130125090000");
        assertThat(trackContracts.get(0).getEdcid()).isEqualTo("dachser");
    }

    @Test
    @DisplayName("AMM testing.")
    public void whenRecieveCorrectAMMFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "amm/P0815-STAT_IFTSTA-4.txt";

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "amm",
                "classpath:/smooks/amm/config-amm.xml",
                "edifact",
                fileName,
                getResource(fileName, "UTF-8")
        );

        assertThat(trackContracts.size()).isOne();

        assertThat(trackContracts.get(0).getRefId()).isEqualTo("279289");
        assertThat(trackContracts.get(0).getRefType()).isEqualTo("XSISHP");
        assertThat(trackContracts.get(0).getStateId()).isEqualTo("50");
        assertThat(trackContracts.get(0).getTimestamp()).isEqualTo("20170329085100");
        assertThat(trackContracts.get(0).getEdcid()).isEqualTo("amm");
    }

    @Test
    @DisplayName("TOF testing.")
    public void whenRecieveCorrectTOFFile_thenParseSuccessful() throws IOException, ParserFailedException {
        String fileName = "tof/93449322.ROTHENBE.20140408.AZMX.39471758.11171400.CSV";

        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                "tof",
                "classpath:/smooks/tof/config-tof.xml",
                "csv",
                fileName,
                getResource(fileName, "windows-1252")
        );

        assertThat(trackContracts.size()).isEqualTo(39);

        assertThat(trackContracts.get(0).getRefId()).isEqualTo("0080828078");
        assertThat(trackContracts.get(0).getRefType()).isEqualTo("DELI");
        assertThat(trackContracts.get(0).getStateId()).isEqualTo("A");
        assertThat(trackContracts.get(0).getStateTextExt()).isEqualTo("Export CPT (Frei Haus) Tschechien Zone 1");
        assertThat(trackContracts.get(0).getTimestamp()).isEqualTo("20140303000000");
        assertThat(trackContracts.get(0).getEdcid()).isEqualTo("tof");
        assertThat(trackContracts.get(0).getName1()).isEqualTo("Kovosluzba OTS a.s.");
        assertThat(trackContracts.get(0).getPcode1()).isEqualTo("277 07");
        assertThat(trackContracts.get(0).getCity()).isEqualTo("Vranany");
        assertThat(trackContracts.get(0).getCountryiso()).isEqualTo("CZ");

        assertThat(trackContracts.get(38).getRefId()).isEqualTo("0080849179");
        assertThat(trackContracts.get(38).getRefType()).isEqualTo("DELI");
        assertThat(trackContracts.get(38).getStateId()).isEqualTo("A");
        assertThat(trackContracts.get(38).getStateTextExt()).isEqualTo("Export CPT (Frei Haus) Tschechien Zone 2");
        assertThat(trackContracts.get(38).getTimestamp()).isEqualTo("20140331000000");
        assertThat(trackContracts.get(38).getEdcid()).isEqualTo("tof");
        assertThat(trackContracts.get(38).getName1()).isEqualTo("TOP CENTRUM - Jaroslav Novak");
        assertThat(trackContracts.get(38).getPcode1()).isEqualTo("533 43");
        assertThat(trackContracts.get(38).getCity()).isEqualTo("Rohovladova Bela");
        assertThat(trackContracts.get(38).getCountryiso()).isEqualTo("CZ");

    }

    private byte[] getResource(String resourseName, String encoding) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(resourseName),
                encoding
        ).getBytes(encoding);
    }
}