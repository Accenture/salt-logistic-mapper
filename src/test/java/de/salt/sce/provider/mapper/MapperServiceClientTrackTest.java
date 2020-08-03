package de.salt.sce.provider.mapper;

import de.salt.sce.provider.model.pojo.UtilObjectResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperServiceClientTrackTest {

    @Test
    public void testProjectStructure() {

        assertThat(MapperServiceClientTrack.buildUtilObjectResponse()).isInstanceOf(UtilObjectResponse.class);
    }
}