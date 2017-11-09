#!/bin/sh

hostip=$(ip route show | awk '/default/ {print $3}')
echo $hostip
