#!/bin/bash

CURRENT_DIR_NAME=$(dirname "${BASH_SOURCE[0]}")

echo "CURRENT_DIR_NAME=${CURRENT_DIR_NAME}"

source "${CURRENT_DIR_NAME}/../../../../env/common.sh"

DATA_ARCHIVE_TYPE="data-archive-db-store"
PORTAL_VERSION="7.4.13"

function assert_document_library_not_populated {
	local document_library_dir="${LIFERAY_HOME}/data/document_library"

	if [[ ! -d ${document_library_dir} ]]
	then
		return 0
	fi

	if [[ $(ls -A ${document_library_dir} | wc -l) -gt 1 ]]
	then
		echo "Unable to confirm the database store was used, ${document_library_dir} is populated."

		exit 1
	fi
}

function main {
	set -ex

	update_portal_ext_properties

	cd "${_PORTAL_PROJECT_DIR}"

	ant -f build-test.xml \
		-Ddata.archive.type="${DATA_ARCHIVE_TYPE}" \
		-Dkeep.cached.app.server.data=true \
		-Dportal.version="${PORTAL_VERSION}" \
		-Dskip.get.testcase.database.properties=true \
		rebuild-legacy-database

	ant -f build-test.xml upgrade-legacy-database

	assert_clean_upgrade_log

	default_set_up

	assert_document_library_not_populated
}

main "${@}"