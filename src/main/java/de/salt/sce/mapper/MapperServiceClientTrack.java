package de.salt.sce.mapper;

import com.typesafe.config.Config;
import de.salt.sce.mapper.parser.MessageParser;
import de.salt.sce.mapper.server.communication.model.MapperRequest;
import de.salt.sce.mapper.server.communication.model.MapperResponses;
import de.salt.sce.mapper.server.communication.model.MapperResponses.InternalResponse;
import de.salt.sce.util.TextFileDetector;
import scala.Option;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Map;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static akka.http.javadsl.model.StatusCodes.BAD_REQUEST;
import static akka.http.javadsl.model.StatusCodes.OK;

import static scala.collection.JavaConverters.mapAsJavaMapConverter;

public class MapperServiceClientTrack {

    /**
     * returns TrackResponseProtocol.
     *
     * @return {@link InternalResponse}
     */
    public static InternalResponse buildResponse(MapperRequest requestData, Config config) throws UnsupportedEncodingException {

        MessageParser messageParser = new MessageParser();
        String serviceConfigurationName = requestData.serviceName().toLowerCase();
        String smooks_config = config.getString("sce.track.mapper.smooks.config-files-path") + "/" + serviceConfigurationName + "/" + requestData.configFile();

        Map<String, String> mapSuccess = new HashMap<>();
        Map<String, String> mapErrors = new HashMap<>();

        mapAsJavaMapConverter(requestData.files()).asJava().forEach(
                (fileName, fileContent) -> {
                    try {
                        if(!fileContent.startsWith("Error")){
                            if(TextFileDetector.isText(fileContent)) {
                                Optional<String> smooksEncodedObject = messageParser.parseFile(
                                        serviceConfigurationName,
                                        smooks_config,
                                        requestData.messageType(),
                                        fileName,
                                        fileContent.getBytes(requestData.encoding())
                                );

                                smooksEncodedObject
                                        .map(o -> mapSuccess.put(fileName, o))
                                        .orElseGet(() -> mapErrors.put(fileName, "Unknown message type"));
                            } else {
                                mapErrors.put(fileName, String.format("Not able to map binary file %s",fileName));
                            }
                        } else {
                            mapErrors.put(fileName, fileContent);
                        }

                    } catch (Exception e) {
                        mapErrors.put(fileName, e.getMessage());
                    }
                }
        );

        if (requestData.messageType().equals("csv")) {
            return new InternalResponse(
                    requestData.id(),
                    Option.apply(new MapperResponses.MapperResponseProtocol(
                            mapSuccess,
                            mapErrors
                    )),
                    Option.empty(),
                    OK.intValue()
            );
        } else if (requestData.messageType().equals("edifact")) {
            return new InternalResponse(
                    requestData.id(),
                    Option.empty(),
                    Option.apply(new MapperResponses.MapperResponseProtocol(
                            mapSuccess,
                            mapErrors
                    )),
                    OK.intValue()
            );
        }

        return new InternalResponse(
                requestData.id(),
                Option.empty(),
                Option.empty(),
                BAD_REQUEST.intValue()

        );

    }
}
