package streams;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.KeyValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.lang.Long;


import OGGUSER.metadata;
import OGGUSER.rowdata;
import OGGUSER.outputdata;

public class StreamsTopology {

        Properties props;

        StreamsTopology(Properties props) {
                this.props = props;
        }

        public KafkaStreams getSteams() {

                StreamsBuilder builder = new StreamsBuilder();

                Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                                this.props.getProperty("schema.registry.url"));

                Serde<GenericRecord> genericAvroSerde = new GenericAvroSerde();
                genericAvroSerde.configure(serdeConfig, false); // `false` for record values

                Serde<String> StringSerde = new Serdes.StringSerde();
                StringSerde.configure(serdeConfig, true); // `false` for record values

                KStream<String, GenericRecord> employees = builder
                    .stream("OGGUSER.EMPLOYEES", Consumed.with(StringSerde, genericAvroSerde))
                    .map(
                        new KeyValueMapper<String, GenericRecord, KeyValue<String, GenericRecord>>() {
                                @Override
                                public KeyValue<String, GenericRecord> apply(String k, GenericRecord v) {
                                        String newKey = String.valueOf(v.get("EMPLOYEE_ID"));
                                        return new KeyValue<>(newKey, v);

                                }
                        }
                    )
                    .mapValues(record -> {
                        final metadata metaData =  new metadata(
                                (record.get("table") == null) ? null : String.valueOf(record.get("table")),
                                (record.get("current_ts") == null) ? null : String.valueOf(record.get("current_ts")),
                                (record.get("pos") == null) ? null : String.valueOf(record.get("pos")),
                                (record.get("op_type") == null) ? null : String.valueOf(record.get("op_type")),
                                (record.get("primary_keys") == null) ? null : (List<String>) record.get("primary_keys")
                                // record.get("primary_keys").asList()
                        );
                        
                        // List<String> pkey = (List<String>) record.get("primary_keys");

                        if (String.valueOf(record.get("op_type")).equalsIgnoreCase("D")) {
                                return new outputdata(metaData, null);
                        } else {
                          final rowdata rowData = new rowdata(
                                (record.get("FIRST_NAME") == null) ? null : Long.valueOf(String.valueOf(record.get("EMPLOYEE_ID"))),
                                (record.get("FIRST_NAME") == null) ? null : String.valueOf(record.get("FIRST_NAME")),
                                (record.get("LAST_NAME") == null) ? null : String.valueOf(record.get("LAST_NAME"))
                          );
                          return new outputdata(
                                  metaData,
                                  rowData
                          );
                        }
                    });
                // sink to new topic for downstream consumers
                employees.to("OGGUSER.EMPLOYEES.INGEST", Produced.with(StringSerde, genericAvroSerde));

                return new KafkaStreams(builder.build(), props);
        }
}
