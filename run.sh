#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}

sbt assembly

java -jar target/scala-2.12/ships-assembly-0.0.1-SNAPSHOT.jar