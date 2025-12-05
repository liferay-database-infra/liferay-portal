/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';


export const test = mergeTests(
	applicationsMenuPageTest,
	serverAdministrationPageTest
);

test('Execute Remove Class Name Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Class Name Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove DL Preview Change Tracking Store Content Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove DL Preview Change Tracking Store Content Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove expired journal articles cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove expired journal articles.' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Layout Classed Model Usage Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Layout Classed Model Usage Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Publications Older Than 6 months cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Publications Older Than 6 months' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Published Change Tracking Store Content Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Published Change Tracking Store Content Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Service Component Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Service Component Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Widget Settings from Converted Widget Pages to Content Pages cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Widget Settings from Converted Widget Pages to Content Pages' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Quartz Job Details Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Quartz Job Details Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Analytics Message Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Analytics Message Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Company Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Company Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove User Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove User Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Configuration Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Configuration Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Group Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Group Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove DDM Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove DDM Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove DL File Entry Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove DL File Entry Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Journal Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Journal Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove Null Unicode Content Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove Null Unicode Content Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Remove DDM Storage Link Orphan Data cleanup action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Remove DDM Storage Link Orphan Data' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Fix Counter Values action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Fix Counter Values' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Reset preview and thumbnail files for documents and media action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Reset preview and thumbnail files for documents and media.' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Clean up permissions action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Clean up permissions.' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Clean up orphaned page revision portlet preferences action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Clean up orphaned page revision portlet preferences.' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

    test('Execute Clean up orphaned theme portlet preferences action', async ({
        applicationsMenuPage,
        serverAdministrationPage,
        page
    }) => {
        await applicationsMenuPage.goToServerAdministration();
        const actionRow = page.getByRole('row').filter({ hasText: 'Clean up orphaned theme portlet preferences.' });
        await actionRow.getByRole('button', { name: 'Execute' }).click();
        await expect(page.getByText('Success:Your request completed successfully.')).toBeVisible();
        await applicationsMenuPage.goToServerAdministration();
    });

