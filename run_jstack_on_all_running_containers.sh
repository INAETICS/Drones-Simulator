#!/usr/bin/env bash

get_name (){
    CONTAINERID=$1
    CONTAINERNAME=$(docker inspect --format="{{.Name}}" ${CONTAINERID})
    echo "name: ${CONTAINERNAME}"
}

get_thread (){
    CONTAINERID=$1
    PIDS=$(docker exec ${CONTAINERID} top -b -n1 | grep java | awk '{ print $1 }')
    PIDS_ARRAY=$(echo ${PIDS}| cut -d ' ' -f 7) #This should be the first PID in top (which is on the 7th line)
    echo ${PIDS_ARRAY}
}

to_hex () {
    echo $(echo "obase=16; "$1| bc)
}

get_info (){
    CONTAINERID=$1
    PID=$2
    PROCESSES=$(docker exec ${CONTAINERID} ps H  -q ${PID} -eo "pid lwp %cpu %mem command" | sort -nrk 3,3 | head -n 5)
    for PROCESS in ${PROCESSES}; do
        ID=$(echo ${PROCESS} | awk '{ print $1 }')
        LWP=$(echo ${PROCESS} | awk '{ print $2 }')
        CPU=$(echo ${PROCESS} | awk '{ print $3 }')
        MEM=$(echo ${PROCESS} | awk '{ print $4 }')
        COMMAND=$(echo ${PROCESS} | awk '{for(i=5;i<=NF;i++) print $i }')
        if [ ${LWP} == "LWP" ]; then
            HEX_LWP="LWP_HEX"

        else
            HEX_LWP=$(to_hex ${LWP})
        fi
        printf '%3s %3s %7s %4s %4s %s' ${ID} ${LWP} ${HEX_LWP} ${CPU} ${MEM}
        echo ${COMMAND}
    done

}

echo "To understand the output, please read https://dzone.com/articles/how-analyze-java-thread-dumps.
The output is partitioned for each container with a set of bars (----------------------)
Each container that runs java will show a stack dump and a java process overview. The stack dump is generated with jstack
and the overview is generated with ps. The process overview also contains a HEX LWP. This is the nid in the stack dump.
Please use the first HEX LWP to find the busiest thread and inspect the thread manually more.

"

IFS='
'
for c in `docker ps -q`; do
    name=$(get_name ${c})
    THREAD_ID=$(get_thread ${c})
    if [ ! -z ${THREAD_ID} ]; then
        echo "Get stack for container" ${c} ${name} " and thread_id" ${THREAD_ID}
        get_info ${c} ${THREAD_ID}
        docker exec ${c} jstack ${THREAD_ID}
    else
        echo "No java process found in this container" ${c} ${name}
    fi
    echo
    echo "--------------------------------------------------------"
    echo
done