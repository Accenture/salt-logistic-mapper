package de.salt.sce.mapper.server.pojo;

import java.util.HashMap;
import java.util.Map;

public class UtilObjectResponse {
    public Map<String, String> successes;
    public Map<String, String> errors;

    public UtilObjectResponse() {
        this.successes = new HashMap<>();
        this.errors = new HashMap<>();
    }
}