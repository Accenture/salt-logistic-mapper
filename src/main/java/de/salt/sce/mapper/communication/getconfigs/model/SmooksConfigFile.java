package de.salt.sce.mapper.communication.getconfigs.model;

public class SmooksConfigFile {
    private String fileName;
    private String fileContent;

    public SmooksConfigFile(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

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
