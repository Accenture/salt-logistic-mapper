package de.salt.sce.mapper.util;

import de.salt.sce.model.csv.PaketCSV;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectSerializerUnitSpec {

    @Test
    public void whenSeliazeObject_thenCanDeserialize() {
        String ort1 = "Würzburg";
        String ort2 = "München";
        PaketCSV paketCSV1 = new PaketCSV();
        paketCSV1.setOrt(ort1);
        PaketCSV paketCSV2 = new PaketCSV();
        paketCSV2.setOrt(ort2);
        List<PaketCSV> paketCSVs = new ArrayList<>();
        paketCSVs.add(paketCSV1);
        paketCSVs.add(paketCSV2);

        byte[] serializesBytes = ObjectSerializer.serialize(paketCSVs);

        Object trackContractsDeserializedObject = ObjectSerializer.deserialize(serializesBytes);

        assertThat(trackContractsDeserializedObject).isInstanceOf(ArrayList.class);

        @SuppressWarnings("unchecked")
        List<PaketCSV> trackContractsDeserialized = (ArrayList<PaketCSV>) trackContractsDeserializedObject;

        assertThat(trackContractsDeserialized).hasSize(2);
        assertThat(trackContractsDeserialized.get(0).getOrt()).isEqualTo(ort1);
        assertThat(trackContractsDeserialized.get(1).getOrt()).isEqualTo(ort2);
    }
}