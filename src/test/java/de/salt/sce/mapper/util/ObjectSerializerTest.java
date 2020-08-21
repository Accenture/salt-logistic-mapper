package de.salt.sce.mapper.util;

import de.salt.sce.mapper.model.TrackContract;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ObjectSerializerTest {

    @Test
    public void whenSeliazeObject_thenCanDeserialize() {
        String refId1 = "REFID1";
        String refId2 = "REFID2";
        List<TrackContract> trackContracts = new ArrayList<>();
        trackContracts.add(createTrackContract(refId1));
        trackContracts.add(createTrackContract(refId2));

        byte[] serializesBytes = ObjectSerializer.serialize(trackContracts);

        Object trackContractsDeserializedObject = ObjectSerializer.deserialize(serializesBytes);

        assertThat(trackContractsDeserializedObject).isInstanceOf(ArrayList.class);

        @SuppressWarnings("unchecked")
        List<TrackContract> trackContractsDeserialized = (ArrayList<TrackContract>) trackContractsDeserializedObject;

        assertThat(trackContractsDeserialized).hasSize(2);
        assertThat(trackContractsDeserialized.get(0).getRefId()).isEqualTo(refId1);
        assertThat(trackContractsDeserialized.get(1).getRefId()).isEqualTo(refId2);
    }

    private TrackContract createTrackContract(String refId) {
        TrackContract trackContract = new TrackContract();
        trackContract.setRefId(refId);
        return trackContract;
    }
}