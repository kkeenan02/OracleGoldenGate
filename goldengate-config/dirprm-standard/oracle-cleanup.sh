#!/bin/sh

# delete goldengate config
ggsci <<- EOF
    STOP EXTRACT PUMP
    DELETE EXTRACT PUMP
    STOP EXTRACT EXT
    DELETE EXTRACT EXT
    DELETE CREDENTIALSTORE
    EXIT
EOF

# delete files
rm -f ./dirdat/et*

# delete 
sqlplus SYS/w@ORCLPDB1 as SYSDBA <<- EOF
    DROP USER ogguser CASCADE;
    COMMIT;
    EXIT
EOF

# delete c##ggadmin user
sqlplus / as SYSDBA <<- EOF
    DROP USER c##ggadmin CASCADE;
    COMMIT;
    EXIT;
EOF