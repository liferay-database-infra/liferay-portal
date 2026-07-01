#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-portal"
PORTAL_VERSION="6.2.5"

function main {
	set -ex

	cd "${_PORTAL_PROJECT_DIR}"

	ant -f build-test.xml \
		-Ddata.archive.type=${DATA_ARCHIVE_TYPE} \
		-Dkeep.cached.app.server.data=true \
		-Dportal.version=${PORTAL_VERSION} \
		-Dskip.get.testcase.database.properties=true \
		rebuild-legacy-database

	local upgrade_exit_code=0
	ant -f build-test.xml upgrade-legacy-database || upgrade_exit_code=$?

	assert_clean_upgrade_log

	if [ ${upgrade_exit_code} -ne 0 ]
	then
		echo "Upgrade failed with exit code ${upgrade_exit_code}."

		exit ${upgrade_exit_code}
	fi

	default_set_up
}

main "${@}"
