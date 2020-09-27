#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
# set -o xtrace

# Set magic variables use by other scripts
__project="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
__scripts=${__project}/scripts
__args=("$@")

source ${__scripts}/actionhandler.sh