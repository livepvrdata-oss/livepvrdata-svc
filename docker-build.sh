#!/bin/bash

this_dir=$(readlink -f $(dirname $0))
base_dir=/$(basename $this_dir)
echo "Running from $this_dir"

if [[ -z $(docker images -q stuckless/sagetv-build) ]]; then
   docker pull stuckless/sagetv-build
fi

if [[ $# > 0 ]]; then
   gradle_cmds="$@"
fi

docker run -u $(id -u) -w $base_dir -t -i -v $this_dir:$base_dir \
  -v ${HOME}:${HOME} -e HOME=$HOME -e GRADLE_USER_HOME=$HOME \
  stuckless/sagetv-build ./gradlew $gradle_cmds
