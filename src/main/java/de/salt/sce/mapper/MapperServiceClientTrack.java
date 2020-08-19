package de.salt.sce.mapper;


import com.typesafe.config.Config;
import de.salt.sce.mapper.model.TrackContract;
import de.salt.sce.mapper.parser.MessageParser;
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest;
import de.salt.sce.mapper.server.communication.model.Responses.TrackResponseProtocol;
import scala.Tuple2;
import scala.collection.immutable.HashMap;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.String.format;

public class MapperServiceClientTrack {

    /**
     * returns TrackResponseProtocol.
     *
     * @return {@link TrackResponseProtocol}
     */
    public static TrackResponseProtocol buildResponse(TrackProviderRequest requestData, Config config) throws UnsupportedEncodingException {

        String serviceConfigurationName = requestData.configName();

        //TODO change to lesen von request(TrackProviderRequest) ? or anower way
        String message_type = "edifact";
        String encoding = "windows-1252";
        //TODO configurable from conf
        String smooks_config = "/smooks/config-ups.xml";

        Tuple2<String, String> line = requestData.lines().head();
        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                serviceConfigurationName,
                smooks_config,
                message_type,
                line._1,
                line._2.getBytes(encoding)
        );

        //TODO trackContracts -> TrackResponseProtocol
        return new TrackResponseProtocol(
                new HashMap<String, String>(),
                new HashMap<String, String>()
        );
    }
}
