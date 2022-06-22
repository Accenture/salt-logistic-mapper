package de.salt.sce.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static de.salt.sce.util.TextFileDetector.isText;
import static org.assertj.core.api.Assertions.assertThat;

public class TextFileDetectorUnitSpec {

    @Test
    public void whenIsJpgFile_thenIsBinary() throws Exception {
        String resourceName = "a_jpg_file.jpg";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsTarFile_thenIsBinary() throws Exception {
        String resourceName = "a_tar_file.tar";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsZipFile_thenIsBinary() throws Exception {
        String resourceName = "a_zip_file.zip";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsZipFileWithTxtExt_thenIsBinary() throws Exception {
        String resourceName = "a_zip_file_with_txt_ext.txt";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsGzFile_thenIsBinary() throws Exception {
        String resourceName = "a_gz_file.log.gz";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsPdfFile_thenIsBinary() throws Exception {
        String resourceName = "a_pdf_file.pdf";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsPdfFileContainstooMuchAlphabetical_thenIsBinary() throws Exception {
        String resourceName = "EasySpedDE VALIDATE - Manuale Utente.pdf";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsTifFile_thenIsBinary() throws Exception {
        String resourceName = "a_tif_file.tif";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isFalse();
    }

    @Test
    public void whenIsAmmDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "AMM_DE_IFTSTA.002";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDacDeFile_thenINotsBinary() throws Exception {
        String resourceName = "DAC_DE.txt";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDbsDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "DBS_DE.arc";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDhfDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "DHF_DE.txt";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDhlDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "DHL_DE.txt";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDpdDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "DPD_DE";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDpdDeFileSmallFile_thenIsNotBinary() throws Exception {
        String resourceName = "DPD_DE.sem";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsDsvDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "DSV_DE.new";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsEmoDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "EMO_DE.txt";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsGeiDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "GEI_DE";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsGlsAtFile_thenIsNotBinary() throws Exception {
        String resourceName = "GLS_AT.018";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsGlsDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "GLS_DE.001";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsHepDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "HEP_DE.014";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsHonDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "HON_DE.TXT";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsKunDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "KUN_DE.EDI";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsSanDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "SAN_DE.CSV";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsTofDeFile_thenIsNotBinary() throws Exception {
        String resourceName = "TOF_DE.ROTHENBE.20180710.RZHX.95054691.08104200.CSV";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    @Test
    public void whenIsUPSFile_thenIsNotBinary() throws Exception {
        String resourceName = "UPS";
        String file = getFileContent(resourceName);

        assertThat(isText(file)).isTrue();
    }

    private String getFileContent(String resourceName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(resourceName).toURI())), StandardCharsets.UTF_8);

    }
}