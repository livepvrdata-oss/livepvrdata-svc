#!/bin/bash

this_dir=$(readlink -f $(dirname $0))
echo "Running from $this_dir"

#look for private environment
[[ -f $this_dir/.env ]] && source $this_dir/.env

if [[ $# > 0 ]]; then
   gradle_cmds="$@"
fi

docker run -u $(id -u) -w $this_dir -t -i -v $this_dir:$this_dir \
  -v ${HOME}:${HOME} -v $GRADLE_USER_HOME:$GRADLE_USER_HOME \
  -e HOME=$HOME -e GRADLE_USER_HOME=$GRADLE_USER_HOME \
  openjdk:7-jdk ./gradlew $gradle_cmds
