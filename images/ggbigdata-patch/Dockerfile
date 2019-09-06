FROM oracle/goldengate-standard-bigdata:12.3.0.1.2

# # kafka handler setup
# RUN mkdir -p /var/lib/kafka/libs \
#   && chown -R oracle:oinstall /var/lib/kafka/libs \
#   && curl -o "/var/lib/kafka/libs/kafka-clients-2.1.1.jar" https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/2.1.1/kafka-clients-2.1.1.jar \
#   && curl -o "/var/lib/kafka/libs/lz4-1.2.0.jar" https://repo1.maven.org/maven2/net/jpountz/lz4/lz4/1.2.0/lz4-1.2.0.jar \
#   && curl -o "/var/lib/kafka/libs/slf4j-api-1.7.26.jar" https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.26/slf4j-api-1.7.26.jar \
#   && curl -o "/var/lib/kafka/libs/snappy-java-1.1.1.6.jar" https://repo1.maven.org/maven2/org/xerial/snappy/snappy-java/1.1.1.6/snappy-java-1.1.1.6.jar \
#   && curl -o "/var/lib/kafka/libs/kafka-avro-serializer-5.2.2.jar" http://packages.confluent.io/maven/io/confluent/kafka-avro-serializer/5.2.2/kafka-avro-serializer-5.2.2.jar \
#   && find /var/lib/kafka/libs/ -type f -iname "*.jar" -exec chmod  755 {} \;

# kafka connect setup

COPY kafka-connect/bin/ogg-kafka-connect-1.0.jar /u01/app/ogg/ggjava/resources/lib

RUN curl -o "/u01/app/ogg/ggjava/lib/connect-api-2.1.1.jar" https://repo1.maven.org/maven2/org/apache/kafka/connect-api/2.1.1/connect-api-2.1.1.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/connect-json-2.1.1.jar" https://repo1.maven.org/maven2/org/apache/kafka/connect-json/2.1.1/connect-json-2.1.1.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jackson-annotations-2.8.5.jar" https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.8.5/jackson-annotations-2.8.5.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jackson-core-2.8.5.jar" https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.8.5/jackson-core-2.8.5.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jackson-databind-2.8.5.jar" https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.8.5/jackson-databind-2.8.5.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jopt-simple-5.0.3.jar" https://repo1.maven.org/maven2/net/sf/jopt-simple/jopt-simple/5.0.3/jopt-simple-5.0.3.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/kafka_2.12-2.1.1.jar" https://repo1.maven.org/maven2/org/apache/kafka/kafka_2.12/2.1.1/kafka_2.12-2.1.1.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/kafka-clients-2.1.1.jar" https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/2.1.1/kafka-clients-2.1.1.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/log4j-1.2.17.jar" https://repo1.maven.org/maven2/log4j/log4j/1.2.17/log4j-1.2.17.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/lz4-1.3.0.jar" https://repo1.maven.org/maven2/net/jpountz/lz4/lz4/1.3.0/lz4-1.3.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/metrics-core-2.2.0.jar" https://repo1.maven.org/maven2/com/yammer/metrics/metrics-core/2.2.0/metrics-core-2.2.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/scala-library-2.11.11.jar" https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.11.11/scala-library-2.11.11.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/scala-parser-combinators_2.11-1.0.4.jar" https://repo1.maven.org/maven2/org/scala-lang/modules/scala-parser-combinators_2.11/1.0.4/scala-parser-combinators_2.11-1.0.4.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/slf4j-api-1.7.25.jar" https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/slf4j-log4j12-1.7.25.jar" https://repo1.maven.org/maven2/org/slf4j/slf4j-log4j12/1.7.25/slf4j-log4j12-1.7.25.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/snappy-java-1.1.2.6.jar" https://repo1.maven.org/maven2/org/xerial/snappy/snappy-java/1.1.2.6/snappy-java-1.1.2.6.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/zkclient-0.10.jar" https://repo1.maven.org/maven2/com/101tec/zkclient/0.10/zkclient-0.10.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/zookeeper-3.4.10.jar" https://repo1.maven.org/maven2/org/apache/zookeeper/zookeeper/3.4.10/zookeeper-3.4.10.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/avro-1.7.7.jar" https://repo1.maven.org/maven2/org/apache/avro/avro/1.7.7/avro-1.7.7.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/common-config-5.1.0.jar" http://packages.confluent.io/maven/io/confluent/common-config/5.1.0/common-config-5.1.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/commons-compress-1.4.1.jar" https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.4.1/commons-compress-1.4.1.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/common-utils-5.1.0.jar" http://packages.confluent.io/maven/io/confluent/common-utils/5.1.0/common-utils-5.1.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jackson-core-asl-1.9.13.jar" https://repo1.maven.org/maven2/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jackson-mapper-asl-1.9.13.jar" https://repo1.maven.org/maven2/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/jline-0.9.94.jar" https://repo1.maven.org/maven2/jline/jline/0.9.94/jline-0.9.94.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/kafka-avro-serializer-5.1.0.jar" http://packages.confluent.io/maven/io/confluent/kafka-avro-serializer/5.1.0/kafka-avro-serializer-5.1.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/kafka-schema-registry-client-5.1.0.jar" http://packages.confluent.io/maven/io/confluent/kafka-schema-registry-client/5.1.0/kafka-schema-registry-client-5.1.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/netty-3.7.0.Final.jar" https://repo1.maven.org/maven2/io/netty/netty/3.7.0.Final/netty-3.7.0.Final.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/paranamer-2.3.jar" https://repo1.maven.org/maven2/com/thoughtworks/paranamer/paranamer/2.3/paranamer-2.3.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/xz-1.0.jar" https://repo1.maven.org/maven2/org/tukaani/xz/1.0/xz-1.0.jar \
  && curl -o "/u01/app/ogg/ggjava/lib/kafka-connect-avro-converter-5.1.0.jar" http://packages.confluent.io/maven/io/confluent/kafka-connect-avro-converter/5.1.0/kafka-connect-avro-converter-5.1.0.jar \
  && find /u01/app/ogg/ggjava/lib/ -type f -iname "*.jar" -exec chmod  755 {} \;