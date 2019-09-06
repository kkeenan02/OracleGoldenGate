package streams;

import com.google.gson.Gson;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static spark.Spark.*;

public class apiController {

    KafkaStreams streams;

    apiController(KafkaStreams streams) {
        this.streams = streams;
    }

    private final Gson gson = new Gson();
    private JsonParser parser = new JsonParser();

    private String getKeyValuesAsJson(String store) {
        ReadOnlyKeyValueStore<String, GenericRecord> stateStore = streams.store(store, QueryableStoreTypes.<String, GenericRecord>keyValueStore());
        KeyValueIterator<String, GenericRecord> stateData = stateStore.all();
        List<KeyValue<String, JsonElement>> keyValues = new ArrayList<>();
        while (stateData.hasNext()) {
            KeyValue<String, GenericRecord> row = stateData.next();
            keyValues.add( new KeyValue<>(row.key, parser.parse(String.valueOf(row.value))) );
        }
        return gson.toJson(keyValues);
    }

    private String getValueByKeyAsJson(String store, String key) {
        ReadOnlyKeyValueStore<String, GenericRecord> stateStore = streams.store(store, QueryableStoreTypes.<String, GenericRecord>keyValueStore());
        GenericRecord value = stateStore.get(key);
        if (value == null) {
            KeyValue<String, String> out = new KeyValue<>(key, "Key not found");
            return gson.toJson(out);
        } else {
            KeyValue<String, JsonElement> out = new KeyValue<>(key, parser.parse(String.valueOf(value)));
            return gson.toJson(out);
        }
    }

    public void startApiController() {

        path("/api/tables", () -> {
            before("/*", (q, a) -> System.out.println("Received api call"));
            
            get("/employees", (request, response) -> {
                String stateStore = "EMPLOYEES-TABLE";
                String res = getKeyValuesAsJson(stateStore);
                response.type("application/json");
                return res;
            });

            get("/employees/:key", (request, response) -> {
                String stateStore = "EMPLOYEES-TABLE";
                String key = request.params(":key");
                String res = getValueByKeyAsJson(stateStore, key);
                response.type("application/json");
                return res;
            });
    });

    }
}