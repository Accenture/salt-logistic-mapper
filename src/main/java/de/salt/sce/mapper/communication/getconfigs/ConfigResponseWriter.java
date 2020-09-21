package de.salt.sce.mapper.communication.getconfigs;

import de.salt.sce.modelftp.provider.model.Responses.InternalSmooksFilesResponse;
import de.salt.sce.modelftp.provider.model.SmooksConfigFile;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;

/**
 * Writing smooks configuration files in given path.
 */
public class ConfigResponseWriter {

    private static final String PLACEHOLDER = "PATH_TO_FOLDER";

    /**
     * Storing smooks configuration files in local path.
     * Replacing in config files PLACEHOLDER to Absolute path to file.
     * This needs for smooks. Sometimes smooks configs contain 2 or more files, with links( or include) another files.
     *
     * @param rootFolder                  Path in local filesystem
     * @param internalSmooksFilesResponse {@link InternalSmooksFilesResponse} content to store.
     */
    public static void replaceAndWrite(String rootFolder, InternalSmooksFilesResponse internalSmooksFilesResponse) throws IOException {
        String path = createFilePath(rootFolder, internalSmooksFilesResponse);
        for (SmooksConfigFile smooksFile : internalSmooksFilesResponse.extResponse().files()) {
            writeStringToFile(
                    new File(path + "/" + smooksFile.fileName()),
                    smooksFile.fileContent().replaceFirst(PLACEHOLDER, path),
                    UTF_8
            );
        }
    }

    private static String createFilePath(String rootFolder, InternalSmooksFilesResponse internalSmooksFilesResponse) {
        return rootFolder + "/" + internalSmooksFilesResponse.extResponse().name();
    }
}