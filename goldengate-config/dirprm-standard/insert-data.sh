#!/bin/sh

# echo EXIT | sqlplus ogguser@ORCLPDB1/w @dirprm/real-data.sql
echo EXIT | sqlplus ogguser@ORCLPDB1/w @$ORACLE_HOME/demo/schema/human_resources/hr_popul.sql