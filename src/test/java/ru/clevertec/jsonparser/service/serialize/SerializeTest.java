package ru.clevertec.jsonparser.service.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.clevertec.jsonparser.model.Group;
import ru.clevertec.jsonparser.model.Passport;
import ru.clevertec.jsonparser.model.Person;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class SerializeTest {
    private final Group group = new Group(1, "Java Learning", List.of(
            new Person(1, "Val", 33,
                    new Passport(1, true, "314535252A14PB15", 'q', Map.of(1L, "first"))),
            new Person(2, "Kate", 27,
                    new Passport(2, true, "319347472A14PB26", 'f', Map.of(2L, "second", 3L, "third")))));


    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Custom serialization")
    void checkExternalLibraryHasTheSameResult() throws JsonProcessingException {
        String serializedActual = Serialize.serialize(group);
        String serializedByJackson = mapper.writeValueAsString(group);
        assertThat(serializedActual).isEqualTo(serializedByJackson);
    }
}
