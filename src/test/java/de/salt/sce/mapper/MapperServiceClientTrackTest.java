package de.salt.sce.mapper;

import de.salt.sce.mapper.server.communication.model.Requests;
import de.salt.sce.mapper.server.communication.model.Responses;
import de.salt.sce.mapper.server.pojo.UtilObjectResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperServiceClientTrackTest {

    @Test
    public void testProjectStructure() {
        Assertions.assertThat(MapperServiceClientTrack.buildUtilObjectResponse(null)).isInstanceOf(Responses.TrackResponseProtocol.class);
    }
}