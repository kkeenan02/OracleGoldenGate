package streams;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.time.Instant;

public class stateTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
      GenericRecord value = (GenericRecord) record.value();
      GenericRecord metadata = (GenericRecord) value.get("metadata");
      String sysTime = (metadata.get("current_ts") == null) ? null : String.valueOf(metadata.get("current_ts"));
      Instant timestamp = Instant.parse(sysTime + "Z");
      return (long) timestamp.toEpochMilli();
    }

  }