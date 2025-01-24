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
	}

	@Override
	public T get() {
		T value = _inheritableThreadLocal.get();

		if (value == null) {
			value = initialValue();

			set(value);
		}

		return value;
	}

	@Override
	public void remove() {
		_inheritableThreadLocal.remove();
	}

	@Override
	public void set(T value) {
		_inheritableThreadLocal.set(value);
	}

	public SafeCloseable setValueWithSafeCloseable(T value) {
		set(value);

		return () -> setWithSafeCloseable(value);
	}

	private final InheritableThreadLocal<T> _inheritableThreadLocal =
		new InheritableThreadLocal<>();

}