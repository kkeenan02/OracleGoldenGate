#!/bin/sh

# set up archive log and user schema
sqlplus / as SYSDBA <<- EOF
    SHUTDOWN IMMEDIATE;
    STARTUP MOUNT EXCLUSIVE;
    ALTER DATABASE ARCHIVELOG;
    ALTER DATABASE OPEN;
    CREATE USER c##ggadmin IDENTIFIED BY w
      DEFAULT TABLESPACE users
      TEMPORARY TABLESPACE temp;
    GRANT DBA TO c##ggadmin container=all;
    EXEC dbms_goldengate_auth.grant_admin_privilege('c##ggadmin',container=>'all');
    ALTER SYSTEM SET ENABLE_GOLDENGATE_REPLICATION=true SCOPE=both;
    ALTER DATABASE ADD SUPPLEMENTAL LOG DATA;
    ALTER DATABASE FORCE LOGGING;
    ALTER SYSTEM SWITCH LOGFILE;
    EXIT;
EOF

sqlplus SYS/w@ORCLPDB1 as SYSDBA <<- EOF
    CREATE USER ogguser IDENTIFIED BY w;
    GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE, CREATE TABLE, CREATE VIEW, CREATE MATERIALIZED VIEW TO ogguser;
    EXIT;
EOF

# alter NLS_DATE_FORMAT
sqlplus ogguser@ORCLPDB1/w <<- EOF
    ALTER SESSION SET nls_date_format = 'dd/mm/yyyy';
EOF

# define schemas
# echo EXIT | sqlplus ogguser@ORCLPDB1/w @dirprm/ss_schemas.sql
echo EXIT | sqlplus ogguser@ORCLPDB1/w @$ORACLE_HOME/demo/schema/human_resources/hr_cre.sql

ggsci <<- EOF
    ADD CREDENTIALSTORE
    ALTER CREDENTIALSTORE ADD USER c##ggadmin@ORCLCDB PASSWORD w ALIAS c##ggadmin
    DBLOGIN USERIDALIAS c##ggadmin
    ADD SCHEMATRANDATA ORCLPDB1.ogguser ALLCOLS
    ADD EXTRACT ext, INTEGRATED TRANLOG, BEGIN NOW
    ADD EXTTRAIL ./dirdat/et, EXTRACT ext
    REGISTER EXTRACT ext DATABASE CONTAINER (ORCLPDB1)
    ADD EXTRACT pump, EXTTRAILSOURCE ./dirdat/et
    ADD RMTTRAIL ./dirdat/pm, EXTRACT pump
    START EXTRACT EXT
    START EXTRACT PUMP
    EXIT
EOF