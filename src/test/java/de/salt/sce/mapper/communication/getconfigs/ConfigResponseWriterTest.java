package de.salt.sce.mapper.communication.getconfigs;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigResponseWriterTest {

    @Test
    public void whenGivenCorrectRootPath_thenCreateSmooksFilesWithCorrectPathInPlaceholder() throws IOException {
        File file = File.createTempFile("smooks", "smooks");
        String rootPath = file.getParent().replace("\\", "/");

        ConfigResponse configResponse = createConfigResponse();

        ConfigResponseWriter.replaceAndWrite(
                rootPath,
                configResponse
        );

        configResponse.getFiles().forEach(
                sf -> {
                    try {
                        String fileContent = new String(Files.readAllBytes(Paths.get(rootPath + "/" + configResponse.getName() + "/" + sf.getFileName())), StandardCharsets.UTF_8);
                        assertThat(fileContent).contains(rootPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private ConfigResponse createConfigResponse() {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setName("ups");

        ConfigResponse.SmooksFile smooksFile1 = new ConfigResponse.SmooksFile();
        smooksFile1.setFileName("file1");
        smooksFile1.setFileContent("content 1 content 1 content 1 contPATH_TO_FOLDERent 1 content 1");

        ConfigResponse.SmooksFile smooksFile2 = new ConfigResponse.SmooksFile();
        smooksFile2.setFileName("file2");
        smooksFile2.setFileContent("content 2 content 2 content 2 contPATH_TO_FOLDERent 2 content 2");

        ConfigResponse.SmooksFile smooksFile3 = new ConfigResponse.SmooksFile();
        smooksFile3.setFileName("file3");
        smooksFile3.setFileContent("content 3 content 3 content 3 contPATH_TO_FOLDERent 3 content 3");

        List<ConfigResponse.SmooksFile> smooksFiles = new ArrayList<>();
        smooksFiles.add(smooksFile1);
        smooksFiles.add(smooksFile2);
        smooksFiles.add(smooksFile3);

        configResponse.setFiles(smooksFiles);

        return configResponse;
    }
}