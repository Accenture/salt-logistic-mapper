package de.salt.sce.mapper;

import de.salt.sce.mapper.server.communication.model.Responses;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

public class MapperServiceClientTrackTest {

    @Test
    public void testProjectStructure() throws UnsupportedEncodingException {
        Assertions.assertThat(MapperServiceClientTrack.buildUtilObjectResponse(null, null)).isInstanceOf(Responses.TrackResponseProtocol.class);
    }
}