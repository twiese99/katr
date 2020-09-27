#!/usr/bin/env bash

function init_submodules() {
  source ${__scripts}/init-submodules.sh
}

option="${__args[0]:-}"
case ${option} in
   "submodules")
      init_submodules
      ;;
   "")
     echo "No argument specified"
      ;;
   *)
      echo "Unknown argument: $option"
      exit 1
      ;;
esac