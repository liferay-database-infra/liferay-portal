/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class OrphanReferencesDeadlockRetryTest {

	@Test
	public void testExecuteDeleteRetriesOnDeadlock() throws Exception {
		AtomicInteger callCount = new AtomicInteger();

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.doAnswer(
			invocation -> {
				if (callCount.incrementAndGet() == 1) {
					throw new SQLException("Deadlock", "40001");
				}

				return null;
			}
		).when(
			preparedStatement
		).executeUpdate();

		Connection connection = Mockito.mock(Connection.class);

		Mockito.when(
			connection.prepareStatement(Mockito.anyString())
		).thenReturn(
			preparedStatement
		);

		ReflectionTestUtil.invoke(
			OrphanReferencesDataCleanupUtil.class, "_executeDelete",
			new Class<?>[] {Connection.class, String.class}, connection,
			"delete from foo where bar is null");

		Assert.assertEquals(2, callCount.get());
	}

	@Test
	public void testExecuteDeleteThrowsAfterMaxRetries() throws Exception {
		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.doThrow(
			new SQLException("Deadlock", "40001")
		).when(
			preparedStatement
		).executeUpdate();

		Connection connection = Mockito.mock(Connection.class);

		Mockito.when(
			connection.prepareStatement(Mockito.anyString())
		).thenReturn(
			preparedStatement
		);

		try {
			ReflectionTestUtil.invoke(
				OrphanReferencesDataCleanupUtil.class, "_executeDelete",
				new Class<?>[] {Connection.class, String.class}, connection,
				"delete from foo where bar is null");

			Assert.fail("Expected SQLException after max retries");
		}
		catch (Exception exception) {
			Assert.assertSame(SQLException.class, exception.getClass());
			Assert.assertEquals(
				"40001", ((SQLException)exception).getSQLState());
		}

		int expectedAttempts =
			(int)ReflectionTestUtil.getFieldValue(
				OrphanReferencesDataCleanupUtil.class,
				"_DELETE_MAX_DEADLOCK_RETRIES") + 1;

		Mockito.verify(
			preparedStatement, Mockito.times(expectedAttempts)
		).executeUpdate();
	}

	@Test
	public void testExecuteDeleteThrowsImmediatelyOnNondeadlockError()
		throws Exception {

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.doThrow(
			new SQLException("Constraint violation", "23000")
		).when(
			preparedStatement
		).executeUpdate();

		Connection connection = Mockito.mock(Connection.class);

		Mockito.when(
			connection.prepareStatement(Mockito.anyString())
		).thenReturn(
			preparedStatement
		);

		try {
			ReflectionTestUtil.invoke(
				OrphanReferencesDataCleanupUtil.class, "_executeDelete",
				new Class<?>[] {Connection.class, String.class}, connection,
				"delete from foo where bar is null");

			Assert.fail("Expected SQLException for non-deadlock error");
		}
		catch (Exception exception) {
			Assert.assertSame(SQLException.class, exception.getClass());
			Assert.assertEquals(
				"23000", ((SQLException)exception).getSQLState());
		}

		Mockito.verify(
			preparedStatement, Mockito.times(1)
		).executeUpdate();
	}

}