package de.salt.sce.mapper.communication.getconfigs;

import de.salt.sce.modelftp.provider.model.Responses;
import de.salt.sce.modelftp.provider.model.Responses.InternalSmooksFilesResponse;
import de.salt.sce.modelftp.provider.model.SmooksConfigFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigResponseWriterTest {

    @Test
    public void whenGivenCorrectRootPath_thenCreateSmooksFilesWithCorrectPathInPlaceholder() throws IOException {
        File file = File.createTempFile("smooks", "smooks");
        String rootPath = file.getParent().replace("\\", "/");

        InternalSmooksFilesResponse configResponse = createConfigResponse();

        ConfigResponseWriter.replaceAndWrite(
                rootPath,
                configResponse
        );

        for(int i = 0; i< configResponse.extResponse().files().length; i++) {
            try {
                String fileContent = new String(
                        readAllBytes(get(
                                rootPath + "/" + configResponse.extResponse().name() + "/" + configResponse.extResponse().files()[i].fileName()
                                )
                        ),
                        StandardCharsets.UTF_8
                );
                assertThat(fileContent).contains(rootPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private InternalSmooksFilesResponse createConfigResponse() {

        SmooksConfigFile smooksFile1 = new SmooksConfigFile("file1","content 1 content 1 content 1 contPATH_TO_FOLDERent 1 content 1");
        SmooksConfigFile smooksFile2 = new SmooksConfigFile("file2","content 2 content 2 content 2 contPATH_TO_FOLDERent 2 content 2");
        SmooksConfigFile smooksFile3 = new SmooksConfigFile("file3","content 3 content 3 content 3 contPATH_TO_FOLDERent 3 content 3");

        SmooksConfigFile[] smooksFiles = new SmooksConfigFile[3];
        smooksFiles[0] = smooksFile1;
        smooksFiles[1] = smooksFile2;
        smooksFiles[2] = smooksFile3;

        Responses.SmooksFilesResponseProtocol smooksFilesResponseProtocol  = new Responses.SmooksFilesResponseProtocol("ups",smooksFiles);

        return new InternalSmooksFilesResponse(smooksFilesResponseProtocol, "200");
    }
}