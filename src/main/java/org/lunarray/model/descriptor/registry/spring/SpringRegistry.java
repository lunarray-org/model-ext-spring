/* 
 * Model Tools.
 * Copyright (C) 2013 Pal Hargitai (pal@lunarray.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lunarray.model.descriptor.registry.spring;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.lunarray.common.check.CheckUtil;
import org.lunarray.model.descriptor.registry.Registry;
import org.lunarray.model.descriptor.registry.exceptions.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A Spring based registry.
 * 
 * @author Pal Hargitai (pal@lunarray.org)
 */
public final class SpringRegistry
		implements Registry<String>, ApplicationContextAware {

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringRegistry.class);
	/** Spring application context. */
	private transient ApplicationContext applicationContext;

	/** Default constructor. */
	public SpringRegistry() {
		// Default constructor.
	}

	/** {@inheritDoc} */
	@Override
	public <B> B lookup(final Class<B> clazz) throws RegistryException {
		SpringRegistry.LOGGER.debug("Resolving bean with type {}.", clazz);
		if (CheckUtil.isNull(clazz)) {
			throw new RegistryException(String.format("Class may not be null."));
		}
		if (ArrayUtils.isEmpty(this.applicationContext.getBeanNamesForType(clazz))) {
			throw new RegistryException(String.format("Could not find bean for type '%s'.", clazz));
		} else {
			return this.applicationContext.getBean(clazz);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <B> B lookup(final Class<B> clazz, final String name) throws RegistryException {
		SpringRegistry.LOGGER.debug("Resolving bean with type {} and name {}.", clazz, name);
		if (CheckUtil.isNull(clazz) || CheckUtil.isNull(name)) {
			throw new RegistryException(String.format("Class and name may not be null."));
		}
		final String[] names = this.applicationContext.getBeanNamesForType(clazz);
		boolean beanExists = false;
		for (final String beanName : names) {
			if (name.equals(beanName)) {
				beanExists = true;
			}
		}
		if (beanExists) {
			return this.applicationContext.getBean(name, clazz);
		} else {
			throw new RegistryException(String.format("Could not find bean for type '%s' and name '%s'.", clazz, name));
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object lookup(final String name) throws RegistryException {
		SpringRegistry.LOGGER.debug("Resolving bean with name {}.", name);
		if (CheckUtil.isNull(name)) {
			throw new RegistryException(String.format("Name may not be null."));
		}
		if (this.applicationContext.containsBean(name)) {
			return this.applicationContext.getBean(name);
		} else {
			throw new RegistryException(String.format("Could not find bean for name '%s'.", name));
		}
	}

	/** {@inheritDoc} */
	@Override
	public <B> Set<String> lookupAll(final Class<B> clazz) throws RegistryException {
		SpringRegistry.LOGGER.debug("Resolvig all beans with type {}.", clazz);
		if (CheckUtil.isNull(clazz)) {
			throw new RegistryException("Bean type may not be null.");
		}
		final String[] beanNames = this.applicationContext.getBeanNamesForType(clazz);
		final Set<String> result = new HashSet<String>();
		for (final String name : beanNames) {
			result.add(name);
		}
		SpringRegistry.LOGGER.debug("Resolved {} with type {}.", result, clazz);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
