/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class BaseEhcachePortalCacheManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testNonshardedCacheDBPartitionDisbled() {
		Assume.assumeFalse(DBPartition.isPartitionEnabled());

		_test(false);
	}

	@Test
	public void testShardedCacheDBPartitionEnabled() {
		Assume.assumeTrue(DBPartition.isPartitionEnabled());

		_test(true);
	}

	private void _test(boolean sharded) {
		try {
			PortalCache<? extends Serializable, ? extends Serializable>
				portalCache = _multiVMPortalCacheManager.getPortalCache(
					_PORTAL_CACHE_NAME);

			Assert.assertEquals(sharded, portalCache.isSharded());
		}
		finally {
			_multiVMPortalCacheManager.removePortalCache(_PORTAL_CACHE_NAME);
		}
	}

	private static final String _PORTAL_CACHE_NAME =
		RandomTestUtil.randomString();

	@Inject(
		filter = "component.name=com.liferay.portal.cache.ehcache.internal.MultiVMEhcachePortalCacheManager"
	)
	private PortalCacheManager<? extends Serializable, ? extends Serializable>
		_multiVMPortalCacheManager;

}