package de.salt.sce.mapper;


import com.typesafe.config.Config;
import de.salt.sce.mapper.model.TrackContract;
import de.salt.sce.mapper.parser.MessageParser;
import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest;
import de.salt.sce.mapper.server.communication.model.Responses.TrackResponseProtocol;
import org.apache.commons.codec.binary.Base64;
import scala.Tuple2;
import scala.collection.mutable.HashMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MapperServiceClientTrack {

    /**
     * returns TrackResponseProtocol.
     *
     * @return {@link TrackResponseProtocol}
     */
    public static TrackResponseProtocol buildResponse(TrackProviderRequest requestData, Config config) throws UnsupportedEncodingException {

        String serviceConfigurationName = requestData.configName().toLowerCase();
        String smooks_config = config.getString("sce.track.mapper.smooks.config-files-path") + "/" + serviceConfigurationName + "/" + requestData.configFile();

        HashMap<String, String> mapSucess = new HashMap<>();
        HashMap<String, byte[]> mapErrors = new HashMap<>();

        Tuple2<String, String> line = requestData.lines().head();
        MessageParser messageParser = new MessageParser();
        List<TrackContract> trackContracts = messageParser.parseFile(
                serviceConfigurationName,
                smooks_config,
                requestData.messageType(),
                line._1,
                line._2.getBytes(requestData.encoding())
        );
        mapSucess.put(line._1, Base64.encodeBase64String(serialize(trackContracts)));


        //BaseEncoding.base64().encode(userPassword.getBytes("UTF-8"));
        return new TrackResponseProtocol(
                mapSucess,
                mapErrors
        );
    }



    public static byte[] serialize(Object obj) {
        try {
            try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                    o.writeObject(obj);
                }
                return b.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static Object deserialize(byte[] bytes) {
        try {
            try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
                try(ObjectInputStream o = new ObjectInputStream(b)){
                    return o.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Object();
    }

}
