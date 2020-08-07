package de.salt.sce.mapper;

import de.salt.sce.mapper.server.pojo.UtilObjectResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperServiceClientTrackTest {

    @Test
    public void testProjectStructure() {
        Assertions.assertThat(MapperServiceClientTrack.buildUtilObjectResponse()).isInstanceOf(UtilObjectResponse.class);
    }
}