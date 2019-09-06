#!/bin/sh

# set up replicat
ggsci <<- EOF
    ADD REPLICAT conf, exttrail ./dirdat/pm
    START conf
    EXIT
EOF
