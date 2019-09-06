# Sanding up an Oracle GoldenGate handler

## Introduction

Oracle GoldenGate is an application that facilitates low-impact data replication from Oracle database by sourcing data changes from transaction logs, rather than via queries against the database itself.

## Repository structure

```sh
.
├── architecure/        # Contains diagrams and descriptions of the cdc connector design
├── data-examples/      # Contains example Oracle GoldenGate operations records
├── goldengate-config/  # All goldengate configuration files
├── images/             # All docker files required to spin up the pipeline
|── table-state-example/      # KStreams service for reconstructing table state
└── stream-transform-example/      # KStreams service for reconstructing table state
```

## Before we start

Ensure you have cloned the official Oracle docker images repository to your local system:

```sh
cd images
git clone https://github.com/oracle/docker-images.git
```

## Building the standard required docker images

### *Images required*

1. Oracle Database 12c
2. Oracle GoldenGate Standard Edition 19.1.0.0.1
3. Oracle GoldenGate for Big Date 12.3.2.1.1

Each fo these images will be built as per the instructions provided in the official [Oracle docker images repository](https://github.com/oracle/docker-images).

Make sure to follow the order of the build steps below!

#### 1. Build Oracle Database image

Before we can build the Oracle Database image we need to download the database software from the [oracle website](https://www.oracle.com/technetwork/database/enterprise-edition/downloads/index.html). Accept the license agreement and download the `Oracle Database 12c Release 2 Linux x86-64` file.

Once the download has complete, move the file to `docker-images/OracleDatabase/SingleInstance/dockerfiles/12.2.0.1` directory.

DO NOT UNZIP the file!

Now we can use the build script provided by Oracle to build the database image.

```sh
cd images/oracle/docker-images/OracleDatabase/SingleInstance/dockerfiles
./buildDockerImage.sh -v 12.2.0.1 -e -i
```

#### 2. Build Standard GoldenGate image

Before we can build the standard goldengate image, we again need to download the software from the [Oracle website](https://www.oracle.com/technetwork/middleware/goldengate/downloads/index.html). Again, accept the license agreement and download the `Oracle GoldenGate 19.1.0.0.1 for Oracle on Linux x86-64` file.

Once this file has downloaded, move it to `docker-images/OracleGoldenGate/`.

DO NOT UNZIP the file!

Now we can use the build script provided by Oracle to build the database image.

```sh
cd images/oracle/docker-images/OracleGoldenGate/
BASE_IMAGE=oracle/database:12.2.0.1-ee \
  ./dockerBuild.sh \
  191001_fbo_ggs_Linux_x64_shiphome.zip \
  --build-arg BASE_COMMAND="su -c '/opt/oracle/runOracle.sh' oracle" \
  --tag oracle/db-12.2-goldengate-standard:19.1.0.0.1
```

#### 3. Build GoldenGate for Big Data

Before we can build the standard goldengate image, we again need to download the software from the [Oracle website](https://www.oracle.com/technetwork/middleware/goldengate/downloads/index.html). Again, accept the license agreement and download the `Oracle GoldenGate for Big Data 12.3.2.1.1 on Linux x86-64` file.

Once this file has downloaded, move it to `docker-images/OracleGoldenGate/`.

DO NOT UNZIP the file!

Now we can use the build script provided by Oracle to build the database image.

```sh
cd images/oracle/docker-images/OracleGoldenGate/
BASE_IMAGE="oraclelinux:7-slim" \
  ./dockerBuild.sh \
  OGG_BigData_Linux_x64_12.3.2.1.1.zip \
  --tag oracle/goldengate-standard-bigdata:12.3.0.1.2
```

## Patching the standard GoldenGate image

To ensure our GoldenGate image has the correct permissions at runtime, we need to add two configurations to the image. It is easiest to do this by building a new docker image.

```sh
cd images/ggstandard-patch
docker build -t oracle/db-12.2-goldengate-standard:19.1.0.0.1-patched .
```

## Patching the big data GoldenGate image

Now we can build the patched docker image, making sure the necessary kafka client libraries and dependencies are installed. First we need to download the kafka connect big data handler from [oracle](https://www.oracle.com/technetwork/middleware/goldengate/oracle-goldengate-exchange-3805527.html). Download the file named `Sample Oracle GoldenGate Kafka Connect Adapter`, and copy it to `images/ggbigdata-patch`. This file will need to be unzipped before running the next set of commands:

```sh
cd images/ggbigdata-patch
docker build -t oracle/goldengate-standard-bigdata:12.3.0.1.2-patched .
```

Now we have all of the necessary images for running the oracle stack.

## Running the handler stack

```sh
docker-compose up
```

Once the oracle database has successfully started, you should see the following log in the stdout for `docker-compose`:

```sh
#########################
DATABASE IS READY TO USE!
#########################
```

## Patching the goldengate extractor `tnsnames.ora` file

```sh
docker exec -it extractor cp /prv/tnsnames.ora /opt/oracle/oradata/dbconfig/ORCLCDB/tnsnames.ora
```

## GoldenGate-Standard Setup

+ Shell on to the running extract container as `oracle` user

  ```sh
  docker exec -it extractor su oracle
  ```

+ Root database and GoldenGate-Standard configuration

  ```sh
  sh ./dirprm/oracle-setup.sh
  ```

## GoldenGate-Big-Data Setup

+ Shell on to the running replicat container as `oracle` user

  ```sh
  docker exec -it replicat su oracle
  ```

+ Replicat setup

  ```sh
  sh ./dirprm/ggbd-setup.sh
  ```

## Load data into database

+ From the extractor shell execute the data insert script

  ```sh
  sh ./dirprm/insert-data.sh
  ```

## Viewing the data in kafka

The easiest way to view data is via the print statement in ksql-cli. You can run ksql via:

```sh
docker container run -it --rm \
  --hostname ksql-cli \
  -e STREAMS_BOOTSTRAP_SERVERS=kafka:29092 \
  -e STREAMS_SCHEMA_REGISTRY_HOST=schema-registry \
  -e STREAMS_SCHEMA_REGISTRY_PORT=8081 \
  --network cdc-research_streams-net \
  confluentinc/ksql-cli:5.0.0 /bin/bash
```

Connect to ksql

```sh
ksql http://ksql-server:8088
```

From ksql cli, list topics, and print the one you'd like to see

```sql
list topics;
print 'OGGUSER.POSN_VALUATION' from beginning;
```

## Building and running ingest-dummy service

This java application uses kafka streams to recreate the database table state from the goldengate event stream being published to kafka.

```sh
docker exec -it ingest-dummy /bin/bash
```

Clean, build and run the application

```sh
cd ingest-dummy
gradle clean build run
```

## Building and running the table state api service

```sh
docker exec -it kstreams /bin/bash
```

Clean, build and run the application

```sh
cd operation-composer
gradle clean build run

### References

[https://www.pilosa.com/blog/writing-a-custom-handler-for-goldengate/](https://www.pilosa.com/blog/writing-a-custom-handler-for-goldengate/)
