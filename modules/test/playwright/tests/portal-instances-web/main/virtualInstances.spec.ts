/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {portalInstancesPagesTest} from './fixtures/portalInstancesPagesTest';

const test = mergeTests(apiHelpersTest, loginTest(), portalInstancesPagesTest);

test(
	'LPD-92619 - Exporting a virtual instance shows the exported schema name.',
	{tag: '@LPD-92619'},
	async ({apiHelpers, virtualInstancesPage}) => {
		test.setTimeout(240000);

		const name = getRandomString();

		try {
			await virtualInstancesPage.addNewVirtualInstance(name);

			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(name);

			await virtualInstancesPage.exportVirtualInstance(name);

			await expect(
				virtualInstancesPage.exportInstanceSuccessMessage.or(
					virtualInstancesPage.exportInstanceErrorMessage
				)
			).toBeVisible({timeout: 60000});

			if (
				await virtualInstancesPage.exportInstanceErrorMessage.isVisible()
			) {
				const errorText =
					await virtualInstancesPage.exportInstanceErrorMessage.textContent();

				test.skip(
					errorText?.includes('is not supported for') ?? false,
					'Database does not support partitioning'
				);
			}

			await expect(
				virtualInstancesPage.exportInstanceSuccessMessage
			).toContainText(
				`The instance was exported to the schema lexported_${company.companyId}.`
			);
		}
		finally {
			await virtualInstancesPage.deleteVirtualInstance(name);
		}
	}
);

test(
	'LPD-92619 - Exporting an already-exported virtual instance shows an error.',
	{tag: '@LPD-92619'},
	async ({virtualInstancesPage}) => {
		test.setTimeout(240000);

		const name = getRandomString();

		try {
			await virtualInstancesPage.addNewVirtualInstance(name);

			await virtualInstancesPage.exportVirtualInstance(name);

			await expect(
				virtualInstancesPage.exportInstanceSuccessMessage.or(
					virtualInstancesPage.exportInstanceErrorMessage
				)
			).toBeVisible({timeout: 60000});

			if (
				await virtualInstancesPage.exportInstanceErrorMessage.isVisible()
			) {
				const errorText =
					await virtualInstancesPage.exportInstanceErrorMessage.textContent();

				test.skip(
					errorText?.includes('is not supported for') ?? false,
					'Database does not support partitioning'
				);
			}

			await expect(
				virtualInstancesPage.exportInstanceSuccessMessage
			).toBeVisible();

			await virtualInstancesPage.exportVirtualInstance(name);

			await expect(
				virtualInstancesPage.exportInstanceErrorMessage
			).toContainText('Export failed with message:', {timeout: 60000});
		}
		finally {
			await virtualInstancesPage.deleteVirtualInstance(name);
		}
	}
);
