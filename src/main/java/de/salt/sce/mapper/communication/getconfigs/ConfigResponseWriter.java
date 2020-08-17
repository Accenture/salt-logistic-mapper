package de.salt.sce.mapper.communication.getconfigs;

import de.salt.sce.mapper.communication.getconfigs.ConfigResponse.SmooksFile;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;

/**
 * Writig smooks configuration files in given path
 */
public class ConfigResponseWriter {

    private static final String PLACEHOLDER = "PATH_TO_FOLDER";

    /**
     * Storing smooks configuration files in local path.
     * Replacing in config giles {$PATH} to Absolute path to file.
     * This needs to
     *
     * @param rootFolder     Path in local filesystem
     * @param configResponse {@link ConfigResponse} content to store.
     */
    public static void replaceAndWrite(String rootFolder, ConfigResponse configResponse) throws IOException {
        String path = createFilePath(rootFolder, configResponse);
        for (SmooksFile smooksFile : configResponse.getFiles()) {
            writeStringToFile(
                    new File(path + "/" + smooksFile.fileName),
                    smooksFile.fileContent.replaceFirst(PLACEHOLDER, path),
                    UTF_8
            );
        }
    }

    private static String createFilePath(String rootFolder, ConfigResponse configResponse) {
        return rootFolder + "/" + configResponse.getName();
    }
}