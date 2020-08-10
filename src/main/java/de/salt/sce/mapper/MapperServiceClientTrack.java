package de.salt.sce.mapper;


import de.salt.sce.mapper.server.communication.model.Requests.TrackProviderRequest;
import de.salt.sce.mapper.server.communication.model.Responses.TrackResponseProtocol;
import de.salt.sce.mapper.server.pojo.UtilObjectResponse;
import scala.collection.immutable.HashMap;

public class MapperServiceClientTrack {

    /**
     * returns UtilObjectResponse.
     *
     * @return {@link UtilObjectResponse}
     */
    public static TrackResponseProtocol buildUtilObjectResponse(TrackProviderRequest requestData) {
        return new TrackResponseProtocol(
                new HashMap<String,String>(),
                new HashMap<String,String>()
        );
   }
}
