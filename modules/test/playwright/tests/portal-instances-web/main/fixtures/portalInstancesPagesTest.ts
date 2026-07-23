/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {VirtualInstancesPage} from '../../../../pages/portal-instances-web/VirtualInstancesPage';

const portalInstancesPagesTest = test.extend<{
	virtualInstancesPage: VirtualInstancesPage;
}>({
	virtualInstancesPage: async ({page}, use) => {
		await use(new VirtualInstancesPage(page));
	},
});

export {portalInstancesPagesTest};
