#!/bin/bash

set -e

function gtcp() {
    git cherry-pick "$1"
}

# pkcs11-jss-new
gtcp 32b53f248017f6d0b3cbbe7ecf23a7fe4f2246fe
gtcp 03a36287bc8cecf9e470c38f9ff010141c07b392

# fix-javadocs
gtcp 2299a7f9a7feacc78e07cf9d767a7a0687336021

sed 's/jre-1.8.0-openjdk/java-11/g' ./pki.spec -i
