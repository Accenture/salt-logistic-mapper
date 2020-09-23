package de.salt.sce.mapper.communication.getconfigs.model;

import java.util.List;

public class ConfigResponse {
    private String name;
    private List<SmooksConfigFile> files;
    private String statusCode;

    public ConfigResponse(String name, List<SmooksConfigFile> files, String statusCode) {
        this.name = name;
        this.files = files;
        this.statusCode = statusCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SmooksConfigFile> getFiles() {
        return files;
    }

    public void setFiles(List<SmooksConfigFile> files) {
        this.files = files;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
