#!/bin/sh

# remove goldengate configs
ggsci <<- EOF
    STOP REPLICAT conf
    DELETE REPLICAT conf
    EXIT
EOF

# delete files
rm -f ./dirdat/pm*
