package ru.clevertec.jsonparser.service.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.clevertec.jsonparser.service.testutil.testmodel.Group;
import ru.clevertec.jsonparser.service.testutil.testmodel.GroupTestBuilder;

import static org.assertj.core.api.Assertions.assertThat;


class SerializerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Custom serialization")
    void checkExternalLibraryHasTheSameResult() throws JsonProcessingException {
        Group group = GroupTestBuilder.aGroup().build();
        String serializedActual = Serializer.serialize(group);
        String serializedByJackson = mapper.writeValueAsString(group);
        assertThat(serializedActual).isEqualTo(serializedByJackson);
    }
}
