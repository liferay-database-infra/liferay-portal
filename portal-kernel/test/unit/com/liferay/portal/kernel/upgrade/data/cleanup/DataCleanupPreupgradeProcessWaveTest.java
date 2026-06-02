/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge Avalos
 */
public class DataCleanupPreupgradeProcessWaveTest {

	@Test
	public void testCircularDependencyThrows() {
		DataCleanupPreupgradeProcess a = _newProcess();
		DataCleanupPreupgradeProcess b = _newProcess();

		Map<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			map =
				LinkedHashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						a, DataCleanupPreupgradeProcess.dependsOn(b)
					).put(
						b, DataCleanupPreupgradeProcess.dependsOn(a)
					).build();

		try {
			DataCleanupPreupgradeProcess.getWavedDataCleanupPreupgradeProcesses(
				map);

			Assert.fail("Expected RuntimeException for circular dependency");
		}
		catch (RuntimeException runtimeException) {
			Assert.assertEquals(
				"Circular dependency", runtimeException.getMessage());
		}
	}

	@Test
	public void testDiamondProducesThreeWaves() {
		DataCleanupPreupgradeProcess a = _newProcess();
		DataCleanupPreupgradeProcess b = _newProcess();
		DataCleanupPreupgradeProcess c = _newProcess();
		DataCleanupPreupgradeProcess d = _newProcess();

		Map<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			map =
				LinkedHashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						a, DataCleanupPreupgradeProcess.dependsOn()
					).put(
						b, DataCleanupPreupgradeProcess.dependsOn(a)
					).put(
						c, DataCleanupPreupgradeProcess.dependsOn(a)
					).put(
						d, DataCleanupPreupgradeProcess.dependsOn(b, c)
					).build();

		List<List<DataCleanupPreupgradeProcess>> waves =
			DataCleanupPreupgradeProcess.getWavedDataCleanupPreupgradeProcesses(
				map);

		Assert.assertEquals(waves.toString(), 3, waves.size());

		List<DataCleanupPreupgradeProcess> wave1 = waves.get(0);

		Assert.assertEquals(wave1.toString(), 1, wave1.size());
		Assert.assertSame(a, wave1.get(0));

		List<DataCleanupPreupgradeProcess> wave2 = waves.get(1);

		Assert.assertEquals(wave2.toString(), 2, wave2.size());
		Assert.assertTrue(wave2.contains(b));
		Assert.assertTrue(wave2.contains(c));

		List<DataCleanupPreupgradeProcess> wave3 = waves.get(2);

		Assert.assertEquals(wave3.toString(), 1, wave3.size());
		Assert.assertSame(d, wave3.get(0));
	}

	@Test
	public void testIndependentProcessesFormSingleWave() {
		Map<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			map =
				LinkedHashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						_newProcess(), DataCleanupPreupgradeProcess.dependsOn()
					).put(
						_newProcess(), DataCleanupPreupgradeProcess.dependsOn()
					).put(
						_newProcess(), DataCleanupPreupgradeProcess.dependsOn()
					).build();

		List<List<DataCleanupPreupgradeProcess>> waves =
			DataCleanupPreupgradeProcess.getWavedDataCleanupPreupgradeProcesses(
				map);

		Assert.assertEquals(waves.toString(), 1, waves.size());

		List<DataCleanupPreupgradeProcess> wave1 = waves.get(0);

		Assert.assertEquals(wave1.toString(), 3, wave1.size());
	}

	@Test
	public void testLinearChainProducesOneWavePerProcess() {
		DataCleanupPreupgradeProcess a = _newProcess();
		DataCleanupPreupgradeProcess b = _newProcess();
		DataCleanupPreupgradeProcess c = _newProcess();

		Map<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			map =
				LinkedHashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						a, DataCleanupPreupgradeProcess.dependsOn()
					).put(
						b, DataCleanupPreupgradeProcess.dependsOn(a)
					).put(
						c, DataCleanupPreupgradeProcess.dependsOn(b)
					).build();

		List<List<DataCleanupPreupgradeProcess>> waves =
			DataCleanupPreupgradeProcess.getWavedDataCleanupPreupgradeProcesses(
				map);

		Assert.assertEquals(waves.toString(), 3, waves.size());

		for (List<DataCleanupPreupgradeProcess> wave : waves) {
			Assert.assertEquals(wave.toString(), 1, wave.size());
		}

		List<DataCleanupPreupgradeProcess> wave1 = waves.get(0);

		Assert.assertSame(a, wave1.get(0));

		List<DataCleanupPreupgradeProcess> wave2 = waves.get(1);

		Assert.assertSame(b, wave2.get(0));

		List<DataCleanupPreupgradeProcess> wave3 = waves.get(2);

		Assert.assertSame(c, wave3.get(0));
	}

	private DataCleanupPreupgradeProcess _newProcess() {
		return new DataCleanupPreupgradeProcess() {

			@Override
			protected void doUpgrade() {
			}

		};
	}

}