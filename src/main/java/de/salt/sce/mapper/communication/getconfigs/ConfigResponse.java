package de.salt.sce.mapper.communication.getconfigs;

import java.util.List;

/**
 * Entity class for smooks configuration response.
 */
public class ConfigResponse {
    private String name;
    private List<SmooksFile> files;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SmooksFile> getFiles() {
        return files;
    }

    public void setFiles(List<SmooksFile> files) {
        this.files = files;
    }

    public static class SmooksFile {
        String fileName;
        String fileContent;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }
    }
}
