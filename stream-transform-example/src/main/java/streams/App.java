package streams;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.threadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;

public class App {

        static Properties readProperties() {

                
                Properties props = new Properties();

                // expected props
                List<String> expectedProps = new ArrayList<>();
                expectedProps.add(StreamsConfig.APPLICATION_ID_CONFIG);
                expectedProps.add(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG);
                expectedProps.add(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG);
                expectedProps.add(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG);
                expectedProps.add(StreamsConfig.STATE_DIR_CONFIG);
                expectedProps.add(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG);
                expectedProps.add(StreamsConfig.APPLICATION_SERVER_CONFIG);
                expectedProps.add("schema.registry.url");

                expectedProps.forEach(prop -> {
                        System.out.println(prop.toString());
                });

                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-transform-1");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                props.put("schema.registry.url", "http://schema-registry:8081");

                return props;

        }

        public static void main(String[] args) {

                Properties props = readProperties();
                if (props == null) {
                        System.out.println("Properties is null");
                        System.exit(0);
                }

                KafkaStreams streams = new StreamsTopology(props).getSteams();

                streams.cleanUp();
                streams.start();

                // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
                Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

                // Server load config: sparkjava.com/documentation#embedded-web-server
                port(8080);
                int maxThreads = 8;
                int minThreads = 2;
                int timeOutMillis = 30000;
                threadPool(maxThreads, minThreads, timeOutMillis);

                get("/status", (req, res) -> "ready");

        }
}
