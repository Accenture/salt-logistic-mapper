package de.salt.sce.mapper.communication.getconfigs;

import de.salt.sce.mapper.communication.getconfigs.model.ConfigResponse;
import de.salt.sce.mapper.communication.getconfigs.model.SmooksConfigFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConfigResponseWriterUnitSpec {

    private static final String[] filesContent = {
            "content 1 content 1 content 1 content 1 content 1",
            "content 2 content 2 content 2 content 2 content 2",
            "content 3 content 3 content 3 content 3 content 3"
    };

    @Test
    public void whenGivenCorrectRootPath_thenCreateSmooksFilesWithCorrectPathInPlaceholder() throws IOException {
        File file = File.createTempFile("smooks", "smooks");
        String rootPath = file.getParent().replace("\\", "/");

        ConfigResponse configResponse = createConfigResponse();

        ConfigResponseWriter.write(
                rootPath,
                configResponse
        );

        int fileNumber = 0;
        for(SmooksConfigFile smooksFile: configResponse.getFiles()) {
            try {
                String fileContent = new String(
                        readAllBytes(get(
                                rootPath + "/" + configResponse.getName() + "/" + smooksFile.getFileName()
                                )
                        ),
                        UTF_8
                );
                assertThat(fileContent).contains(filesContent[fileNumber++]);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }

    private ConfigResponse createConfigResponse() {
        SmooksConfigFile smooksFile1 = new SmooksConfigFile("file1",filesContent[0]);
        SmooksConfigFile smooksFile2 = new SmooksConfigFile("file2",filesContent[1]);
        SmooksConfigFile smooksFile3 = new SmooksConfigFile("file3",filesContent[2]);

        List<SmooksConfigFile> smooksFiles = new ArrayList<>();
        smooksFiles.add(smooksFile1);
        smooksFiles.add(smooksFile2);
        smooksFiles.add(smooksFile3);

        return new ConfigResponse("ups", smooksFiles, "200");
    }
}