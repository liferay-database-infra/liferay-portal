/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.lang;

import java.util.function.Supplier;

/**
 * @author István András Dézsi
 */
public class CentralizedInheritableThreadLocal<T>
	extends CentralizedThreadLocal<T> {

	public CentralizedInheritableThreadLocal(
		String name, Supplier<T> supplier) {

		super(name, supplier);

		_inheritableThreadLocal.set(supplier.get());
	}

	@Override
	public void remove() {
		_inheritableThreadLocal.remove();

		super.remove();
	}

	@Override
	public void set(T value) {
		_inheritableThreadLocal.set(value);

		super.set(value);
	}

	public SafeCloseable setValueWithSafeCloseable(T value) {
		T originalValue = get();

		set(value);

		if (originalValue == null) {
			return () -> remove();
		}

		return () -> set(originalValue);
	}

	@Override
	protected T initialValue() {
		return _inheritableThreadLocal.get();
	}

	private final InheritableThreadLocal<T> _inheritableThreadLocal =
		new InheritableThreadLocal<>();

}