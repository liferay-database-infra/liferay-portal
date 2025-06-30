/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldAttributePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldAttributeUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeJakarta;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class UpgradeJakartaDDMFieldAttributeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(Propagation.REQUIRED));

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeDDMFieldAttribute() throws Throwable {
		DDMFieldAttributePersistence ddmFieldAttributePersistence =
			DDMFieldAttributeUtil.getPersistence();

		DDMFieldAttribute ddmFieldAttribute =
			ddmFieldAttributePersistence.create(RandomTestUtil.nextLong());

		ddmFieldAttribute.setLargeAttributeValue(_JAVAX_IMPORT);

		ddmFieldAttribute = ddmFieldAttributePersistence.update(
			ddmFieldAttribute);

		Session session = ddmFieldAttributePersistence.getCurrentSession();

		session.evict(ddmFieldAttribute);

		new UpgradeJakarta(
		).upgrade();

		_entityCache.clearCache();
		_finderCache.clearCache();

		DDMFieldAttribute updatedDDMFieldAttribute =
			ddmFieldAttributePersistence.findByPrimaryKey(
				ddmFieldAttribute.getPrimaryKey());

		Assert.assertNotNull(updatedDDMFieldAttribute);

		Assert.assertEquals(
			_JAKARTA_IMPORT, updatedDDMFieldAttribute.getLargeAttributeValue());

		ddmFieldAttributePersistence.remove(ddmFieldAttribute);
	}

	private static final String _JAKARTA_IMPORT =
		"import jakarta.servlet.test.UpgradeJakartaTest;";

	private static final String _JAVAX_IMPORT =
		"import javax.servlet.test.UpgradeJakartaTest;";

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

}