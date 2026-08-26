#!/usr/bin/env sh

export JAVA_HOME=$JAVA_HOME_17_X64

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a symlink
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls -ld "$PRG"
    link=`expr "$PRG" : '.*->\(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="$(cd "$(dirname \"$PRG\")" >/dev/null 2>&1 && pwd)"

# Standard file naming conventions for Gradle
DEFAULT_JVM_OPTS='\" \"-Xmx64m\" \"-Xms64m\"'
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
export JAVA_OPTS="${JAVA_OPTS} -Xmx2048m"

# Use the maximum available, or set MAX_FD != maximum.
MAX_FD="maximum"

# Tell sh how to deal with all special characters (especially globbing) by using double quotes
set -f
set +e

CLASSPATH=\"$SAVED/gradle/wrapper/gradle-wrapper.jar\"

JAVA_EXE=\"$JAVA_HOME/bin/java\"
if [ ! -x \"$JAVA_EXE\" ] ; then
    JAVA_EXE=java
fi

if ! command -v java &> /dev/null
then
    echo \"ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.\"
    echo \"\"
    echo \"Please set the JAVA_HOME variable in your environment to match the\"
    echo \"location of your Java installation.\"
    exit 1
fi

if [ -z \"$JAVA_HOME\" ] ; then
    echo \"WARNING: JAVA_HOME environment variable is not set.\"
fi

# Split the classpath into an array to handle spaces
IFS=: read -ra CLASSPATH_ARRAY <<< \"$CLASSPATH\"

exec \"$JAVA_EXE\" \"-Dorg.gradle.appname=$APP_BASE_NAME\" \"-classpath\" \"$CLASSPATH\" org.gradle.wrapper.GradleWrapperMain \"$@\"
