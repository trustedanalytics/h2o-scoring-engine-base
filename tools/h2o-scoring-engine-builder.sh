#!/bin/bash

###############################################################################
#
# Copyright (c) 2015 Intel Corporation
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.
# 

###############################################################################
#
#
# Usage: h2o-scoring-engine-builder [-o output file] [-u h2o server user] [-p] <h2o server url> <h2o model name> <path to scoring engine prototype jar>
#
# Downloads model and h2o-genmodel.jar library from h2o server and builds a scoring engine using 
# h2o-scoring-engine-prototype app. 
#



################################################################################
# Prints given text in green
# Arguments:
#   text
# Returns:
#   None
################################################################################
function display_info() {
  local green="$(tput setaf 2)"
  local reset="$(tput sgr0)"
  
  echo -e "${green}$1${reset}"
}


################################################################################
# Requests given URL and writes output to given file
# Arguments:
#   url
#   output file
# Returns:
#   None when succeeded, error message otherwise
################################################################################
function download() {
  local url=$1
  local output_file=$2
  local user=$3
  local password=$4
  
  local http_status_code	
  http_status_code="$(curl -u ${user}:${password} --output ${output_file} --write-out "%{http_code}" ${url})"
  if [[ ${http_status_code} -ne 200 ]]; then
    echo $1' download failed with status code: '${http_status_code}
  fi
}

function main() {

  while getopts ":o:u:p" opt; do
    case $opt in
      o)
        OUTPUT_FILE=$OPTARG
        ;;
      u)
        H2O_USER=$OPTARG
        ;;
      p)
        read -s -p $'Enter password:\n' H2O_PASSWORD
        ;;
      \?)
        echo "Invalid option: -$OPTARG" >&2
        exit 1
        ;;
      :)
        echo "Option -$OPTARG requires an argument." >&2
        exit 1
        ;;
    esac
  done

  shift $((OPTIND-1))

  if [[ $# -lt 3 ]]; then
    echo -e "\nUsage:\n$0 [-o output_file] [-u h2o_server_user] [-p] <h2o server URL> <model name> <h2o scoring engine prototype jar path> \n"
    exit 1
  fi

  readonly H2O_SERVER=$1
  readonly MODEL_NAME=$2
  readonly APPLICATION_PROTOTYPE_PATH="$(readlink -f $3)"
  readonly WORKING_DIR="$(dirname $(readlink -f $0))"
  if [[ -z ${OUTPUT_FILE} ]]; then
    OUTPUT_FILE=h2o-scoring-engine-${MODEL_NAME}.jar
  fi

  model_url=${H2O_SERVER}/3/Models.java/${MODEL_NAME}
  h2o_lib_url=${H2O_SERVER}/3/h2o-genmodel.jar
  model_class_file=${MODEL_NAME}.java
  genmodel_lib='h2o-genmodel.jar'

  #switches to temporary directory
  tmp_dir=$(mktemp -d)
  cd ${tmp_dir}


  #downloads model class
  display_info "Downloading model from ${model_url}:"
  result="$(download ${model_url} ${model_class_file} ${H2O_USER} ${H2O_PASSWORD})"
  if [[ -n "${result}" ]]; then
    #download failed - clean up and exit
    if [[ -e ${model_class_file} ]]; then
      rm ${model_class_file}
    fi
    echo ${result}
    exit 1
  fi
  display_info 'Done.'

  
  #downloads library file
  display_info "Downloading h2o-genmodel.jar from ${h2o_lib_url}:"
  result="$(download ${h2o_lib_url} ${genmodel_lib} ${H2O_USER} ${H2O_PASSWORD})"
  if [[ -n "${result}" ]]; then
    #download failed - clean up and exit
    rm ${model_class_file}
    if [[ -e ${genmodel_lib} ]]; then
      rm ${genmodel_lib}
    fi 	
    echo ${result}
    exit 1
  fi
  display_info 'Done.'

  
  #compiles model and build JAR file
  display_info 'Compiling and packaging model:'
  mkdir class_dir
  javac -cp ${genmodel_lib} ${model_class_file} -d class_dir

  mkdir lib
  jar cvf lib/h2o-model.jar -C class_dir .
  display_info 'Done.'


  #builds engine: attaches model JAR file to engine prototype JAR
  display_info 'Building application:'
  cp ${APPLICATION_PROTOTYPE_PATH} ./${OUTPUT_FILE} 
  
  jar uvf0 ${OUTPUT_FILE} lib/h2o-model.jar
  
  cp ${tmp_dir}/${OUTPUT_FILE} ${WORKING_DIR}
  display_info 'Done.'


  #cleanup
  rm -rf ${tmp_dir}

  display_info "Genarated jar: ${OUTPUT_FILE}"
}

main "$@"

