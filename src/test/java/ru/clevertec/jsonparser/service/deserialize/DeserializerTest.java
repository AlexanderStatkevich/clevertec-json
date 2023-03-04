package ru.clevertec.jsonparser.service.deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.clevertec.jsonparser.service.testutil.testmodel.Group;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class DeserializerTest {

    private static final String objectInJson = "{\"id\":1,\"title\":\"Java Learning\",\"persons\":" +
            "[{\"id\":1,\"name\":\"Val\",\"age\":33,\"passport\":{\"id\":1,\"valid\":true,\"number\":\"314535252A14PB15\",\"character\":\"q\",\"map\":{\"1\":\"first\"}}}," +
            "{\"id\":2,\"name\":\"Kate\",\"age\":27,\"passport\":{\"id\":2,\"valid\":true,\"number\":\"319347472A14PB26\",\"character\":\"f\",\"map\":{\"2\":\"second\",\"3\":\"third\"}}}],\"check\":false}";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Custom deserialization")
    void checkExternalLibraryHasTheSameResult() throws JsonProcessingException {
        Map<String, String> stringStringMap = Parser.parseTo(objectInJson);
        Group deserializedActual = Deserializer.deserialize(stringStringMap, Group.class, null);
        Group deserializedByJackson = mapper.readValue(objectInJson, Group.class);
        assertThat(deserializedActual).isEqualTo(deserializedByJackson);
    }
}
