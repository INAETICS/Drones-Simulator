#!/usr/bin/env bash

get_name (){
    CONTAINERID=$1
    CONTAINERNAME=$(docker inspect --format="{{.Name}}" ${CONTAINERID})
    echo "name: ${CONTAINERNAME}"
}

get_thread (){
    CONTAINERID=$1
    PIDS=$(docker exec ${CONTAINERID} top -b -n1| awk '{ print $1 }')
    PIDS_ARRAY=$(echo $PIDS| cut -d ' ' -f 7) #This should be the first PID in top (which is on the 7th line)
    echo ${PIDS_ARRAY}
}

get_info (){
    CONTAINERID=$1
    PID=$2
    CPU=$(docker exec ${CONTAINERID} ps H -q $PID -eo "pid %cpu %mem" | grep ${PID} | cut -d " " -f 3 | sed -e 's/$/\+/' | tr -d "\n" | sed -e 's/+$/\n/' | bc)
    MEM=$(docker exec ${CONTAINERID} ps H -q $PID -eo "pid %cpu %mem" | grep ${PID} | cut -d " " -f 4 | head -n 1)
    echo ${PID} ${CPU} ${MEM}

}

IFS='
'
for c in `docker ps -q`; do
    name=$(get_name $c)
    THREAD_ID=$(get_thread ${c})
    echo "Get stack for container" ${c} ${name} " and thread_id" ${THREAD_ID}
    docker exec ${c} jstack ${THREAD_ID}
    get_info ${c} ${THREAD_ID}
    echo
    echo "--------------------------------------------------------"
    echo
done