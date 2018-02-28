#!/bin/bash
# 
# This script assumes an Oracle jdk 1.8 or later
#
# Usage: sh ./thread_dump.sh <JBOSS_PID>

SLEEP=5
REPEAT=10
for i in `seq 1 $REPEAT`;
do
  echo "Dump " $i
  jstack -l $1 > thread_dump_$i.txt
  if [ "$i" != "$REPEAT" ]
  then
    echo "Sleeping for" $SLEEP "seconds"
    sleep $SLEEP
  fi
  echo "Prepare archive"
  tar czf thread_archive.tar.gz thread_dump_*
  rm -f thread_dump_*
done    
