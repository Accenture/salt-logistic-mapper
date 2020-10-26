package de.salt.sce.mapper.communication.getconfigs;


import de.salt.sce.mapper.communication.getconfigs.model.ConfigResponse;
import de.salt.sce.mapper.communication.getconfigs.model.SmooksConfigFile;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Writing smooks configuration files in given path.
 */
public class ConfigResponseWriter {
    private static final Logger log = getLogger(ConfigResponseWriter.class);

    /**
     * Storing smooks configuration files in local path.
     *
     * @param rootFolder     Path in local filesystem
     * @param configResponse {@link ConfigResponse} content to store.
     */
    public static void write(String rootFolder, ConfigResponse configResponse) throws IOException {
        String path = createFilePath(rootFolder, configResponse);
        log.trace(format("Create Smooks Configs On Path: %s", path));
        for (SmooksConfigFile smooksFile : configResponse.getFiles()) {
            String filePath = format("%s/%s", path, smooksFile.getFileName());
            log.trace(format("Create Smooks Config File: %s", filePath));
            writeStringToFile(
                    new File(filePath),
                    smooksFile.getFileContent(),
                    UTF_8
            );
        }
    }

    private static String createFilePath(String rootFolder, ConfigResponse configResponse) {
        return rootFolder + "/" + configResponse.getName();
    }
}