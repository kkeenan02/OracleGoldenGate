package streams;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import OGGUSER.rowdata;

public class StreamsTopology {

        Properties props;

        StreamsTopology(Properties props) {
                this.props = props;
        }

        public KafkaStreams getSteams() {

                StreamsBuilder builder = new StreamsBuilder();

                // initialize the genericAvro Serde
                Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                                this.props.getProperty("schema.registry.url"));

                Serde<GenericRecord> genericAvroSerde = new GenericAvroSerde();
                genericAvroSerde.configure(serdeConfig, false); // `false` for record values

                Serde<String> StringSerde = new Serdes.StringSerde();
                StringSerde.configure(serdeConfig, true); // `false` for record values

                Serde<Long> LongSerde = new Serdes.LongSerde();
                LongSerde.configure(serdeConfig, true); 

                // ** K-STREAMS ** //
                
                KTable<String, GenericRecord> employeesTransformed = builder
                    .stream("OGGUSER.EMPLOYEES.INGEST", Consumed.with(StringSerde, genericAvroSerde).withTimestampExtractor(new stateTimestampExtractor()))
                    // .peek( (k, v) -> System.out.println(k + " -> " + v))
                    .mapValues(record -> {
                        GenericRecord row = (GenericRecord) record.get("rowdata");
                        if (row != null) {
                            final rowdata value = new rowdata(
                                (row.get("FIRST_NAME") == null) ? null : Long.valueOf(String.valueOf(row.get("EMPLOYEE_ID"))),
                                (row.get("FIRST_NAME") == null) ? null : String.valueOf(row.get("FIRST_NAME")),
                                (row.get("LAST_NAME") == null) ? null : String.valueOf(row.get("LAST_NAME"))
                            );
                            return (GenericRecord) value;
                        } else {
                            final rowdata value = new rowdata(
                                    null,
                                    null,
                                    null
                            );
                            return (GenericRecord) value;
                        }
                            
                    })
                    .groupByKey()
                    .reduce((aggValue, newValue) -> {
                        if (newValue.get("FIRST_NAME") == null){
                            return null;
                        } else {
                            return newValue;
                        }
                    }, 
                      Materialized.<String, GenericRecord, KeyValueStore<Bytes, byte[]>>as("EMPLOYEES-TABLE").withKeySerde(StringSerde).withValueSerde(genericAvroSerde)
                    );

                    // employeesTransformed.toStream().to("OGGUSER.EMPLOYEES.INGEST.TEST", Produced.with(StringSerde, genericAvroSerde));

                return new KafkaStreams(builder.build(), props);
        }
}
