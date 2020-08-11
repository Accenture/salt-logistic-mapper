package de.salt.sce.mapper;


import com.typesafe.config.Config;
import de.salt.sce.mapper.model.TrackContract;
import de.salt.sce.mapper.parser.MessageParser;
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest;
import de.salt.sce.mapper.server.communication.model.Responses.TrackResponseProtocol;
import de.salt.sce.mapper.server.pojo.UtilObjectResponse;
import scala.Tuple2;
import scala.collection.immutable.HashMap;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.String.format;

public class MapperServiceClientTrack {

    /**
     * returns UtilObjectResponse.
     *
     * @return {@link UtilObjectResponse}
     */
    public static TrackResponseProtocol buildUtilObjectResponse(TrackProviderRequest requestData, Config config) throws UnsupportedEncodingException {


        String serviceConfigurationName = requestData.configName();
        String message_type = config.getString(format("mapper-app.providers.%s.parser.message_type", serviceConfigurationName));
        String encoding = config.getString(format("mapper-app.providers.%s.parser.encoding", serviceConfigurationName));
        String smooks_config = config.getString(format("mapper-app.providers.%s.parser.smooks_config", serviceConfigurationName));

        Tuple2<String, String> line = requestData.lines().head();
        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                serviceConfigurationName,
                smooks_config,
                message_type,
                line._1,
                line._2.getBytes(encoding)
        );

        return new TrackResponseProtocol(
                new HashMap<String, String>(),
                new HashMap<String, String>()
        );
    }
}
