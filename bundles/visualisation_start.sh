#!/bin/bash

ETCD_HOST=localhost ETCD_PORT=4001 java -Dorg.ops4j.pax.logging.DefaultServiceLog.level=OFF -jar visualisation-0.1.jar
