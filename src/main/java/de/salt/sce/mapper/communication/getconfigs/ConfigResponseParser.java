package de.salt.sce.mapper.communication.getconfigs;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.salt.sce.modelftp.provider.model.Responses.InternalSmooksFilesResponse;

/**
 * Parse response body object to Java POJO Objects.
 */
public class ConfigResponseParser {
    /**
     * Parse the response from Parcel Microservice.
     *
     * @param responseBody {@link String}
     * @return {@link InternalSmooksFilesResponse}
     */
    public static InternalSmooksFilesResponse parseResponse(String responseBody) throws JsonSyntaxException {
        return new Gson().fromJson(
                responseBody,
                InternalSmooksFilesResponse.class);
    }
}
