#!/bin/bash

source .gitlab/common.sh
change_timezone_to_berlin

printf "Reduce java security, Reduce DH (Diffie Hellman) key from 2048 to 1024\n"
sed -i "s/DH keySize < 2048, /DH keySize < 1024, /" /etc/crypto-policies/back-ends/java.config

printf "Prepare JUnit Interface\n"
cat .gitlab/junit-interface.txt >> build.sbt
sbt 'testOnly de.salt.*IntegrationSpec'