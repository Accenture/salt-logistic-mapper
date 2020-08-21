package de.salt.sce.mapper;

import com.typesafe.config.Config;
import de.salt.sce.mapper.model.TrackContract;
import de.salt.sce.mapper.parser.MessageParser;
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest;
import de.salt.sce.mapper.server.communication.model.Responses.TrackResponseProtocol;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Map;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static de.salt.sce.mapper.util.ObjectSerializer.serialize;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static scala.collection.JavaConverters.mapAsJavaMapConverter;

public class MapperServiceClientTrack {

    /**
     * returns TrackResponseProtocol.
     *
     * @return {@link TrackResponseProtocol}
     */
    public static TrackResponseProtocol buildResponse(TrackProviderRequest requestData, Config config) throws UnsupportedEncodingException {

        MessageParser messageParser = new MessageParser();
        String serviceConfigurationName = requestData.configName().toLowerCase();
        String smooks_config = config.getString("sce.track.mapper.smooks.config-files-path") + "/" + serviceConfigurationName + "/" + requestData.configFile();

        Map<String, String> mapSucess = new HashMap<>();
        Map<String, String> mapErrors = new HashMap<>();

        mapAsJavaMapConverter(requestData.lines()).asJava().forEach(
                (k, v) -> {
                    try {
                        List<TrackContract> trackContracts = messageParser.parseFile(
                                serviceConfigurationName,
                                smooks_config,
                                requestData.messageType(),
                                k,
                                v.getBytes(requestData.encoding())
                        );
                        mapSucess.put(k, encodeBase64String(serialize(trackContracts)));
                    } catch (Exception e) {
                        mapErrors.put(k, e.getMessage());
                    }
                }
        );

        return new TrackResponseProtocol(
                mapSucess,
                mapErrors
        );
    }
}
