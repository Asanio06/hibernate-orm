/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.engine.spi;

import java.util.Collection;

import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.Incubating;
import org.hibernate.Internal;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EntityCopyObserverFactory;
import org.hibernate.event.spi.EventEngine;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.event.service.spi.EventListenerGroups;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.metamodel.spi.RuntimeMetamodelsImplementor;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.query.spi.QueryParameterBindingTypeResolver;
import org.hibernate.query.sqm.spi.SqmCreationContext;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.sql.ast.spi.ParameterMarkerStrategy;
import org.hibernate.sql.ast.spi.SqlAstCreationContext;
import org.hibernate.sql.results.jdbc.spi.JdbcValuesMappingProducerProvider;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.generator.Generator;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.MappingContext;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Defines the internal contract between the {@link SessionFactory} and the internal
 * implementation of Hibernate.
 *
 * @see SessionFactory
 * @see org.hibernate.internal.SessionFactoryImpl
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public interface SessionFactoryImplementor
		extends MappingContext, SessionFactory, SqmCreationContext, SqlAstCreationContext,
				QueryParameterBindingTypeResolver { //deprecated extension, use MappingMetamodel
	/**
	 * Get the UUID for this {@code SessionFactory}.
	 * <p>
	 * The value is generated as a {@link java.util.UUID}, but kept as a String.
	 *
	 * @return The UUID for this {@code SessionFactory}.
	 *
	 * @see org.hibernate.internal.SessionFactoryRegistry#getSessionFactory
	 */
	String getUuid();

	/**
	 * Access to the name (if one) assigned to the {@code SessionFactory}
	 *
	 * @return The name for the {@code SessionFactory}
	 */
	@Override
	String getName();

	/**
	 * Overrides {@link SessionFactory#openSession()} to widen the return type:
	 * this is useful for internal code depending on {@link SessionFactoryImplementor}
	 * as it would otherwise need to frequently resort to casting to the internal contract.
	 *
	 * @return the opened {@code Session}.
	 */
	@Override
	SessionImplementor openSession();

	@Override
	TypeConfiguration getTypeConfiguration();

	@Override
	default SessionFactoryImplementor getSessionFactory() {
		return this;
	}

	@Override
	default MappingMetamodelImplementor getMappingMetamodel() {
		return getRuntimeMetamodels().getMappingMetamodel();
	}

	@Override
	SessionBuilderImplementor withOptions();

	/**
	 * Get a non-transactional "current" session (used by hibernate-envers)
	 */
	SessionImplementor openTemporarySession();

	@Override
	CacheImplementor getCache();

	@Override
	StatisticsImplementor getStatistics();

	RuntimeMetamodelsImplementor getRuntimeMetamodels();

	/**
	 * Access to the {@code ServiceRegistry} for this {@code SessionFactory}.
	 *
	 * @return The factory's ServiceRegistry
	 */
	ServiceRegistryImplementor getServiceRegistry();

	/**
	 * Get the EventEngine associated with this SessionFactory
	 */
	EventEngine getEventEngine();

	/**
	 * Retrieve fetch profile by name.
	 *
	 * @param name The name of the profile to retrieve.
	 * @return The profile definition
	 */
	FetchProfile getFetchProfile(String name);

	/**
	 * Get the identifier generator for the hierarchy
	 *
	 * @deprecated Only used in one place, will be removed
	 */
	@Deprecated(since = "7", forRemoval = true)
	Generator getGenerator(String rootEntityName);

	EntityNotFoundDelegate getEntityNotFoundDelegate();

	void addObserver(SessionFactoryObserver observer);

	//todo make a Service ?
	CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();

	//todo make a Service ?
	CurrentTenantIdentifierResolver<Object> getCurrentTenantIdentifierResolver();

	/**
	 * The java type to use for a tenant identifier.
	 *
	 * @since 6.4
	 */
	JavaType<Object> getTenantIdentifierJavaType();

	/**
	 * Access to the event listener groups.
	 *
	 * @since 7.0
	 */
	@Internal @Incubating
	EventListenerGroups getEventListenerGroups();

	/**
	 * @return the {@link FastSessionServices} instance associated with this factory
	 *
	 * @deprecated {@link FastSessionServices} belongs to an internal non-SPI package,
	 *             and so this operation is a layer-breaker
	 */
	@Internal @Deprecated(since = "7.0", forRemoval = true)
	FastSessionServices getFastSessionServices();

	/**
	 * @since 7.0
	 */
	@Incubating
	ParameterMarkerStrategy getParameterMarkerStrategy();

	/**
	 * @since 7.0
	 */
	@Incubating
	JdbcValuesMappingProducerProvider getJdbcValuesMappingProducerProvider();

	/**
	 * @since 7.0
	 */
	@Incubating
	EntityCopyObserverFactory getEntityCopyObserver();

	/**
	 * @since 7.0
	 */
	@Incubating
	ClassLoaderService getClassLoaderService();

	/**
	 * @since 7.0
	 */
	@Incubating
	ManagedBeanRegistry getManagedBeanRegistry();

	/**
	 * @since 7.0
	 */
	@Incubating
	EventListenerRegistry getEventListenerRegistry();

	/**
	 * Return an instance of {@link WrapperOptions} which is not backed by a session,
	 * and whose functionality is therefore incomplete.
	 *
	 * @apiNote Avoid using this operation.
	 */
	WrapperOptions getWrapperOptions();

	@Override
	SessionFactoryOptions getSessionFactoryOptions();

	@Override
	FilterDefinition getFilterDefinition(String filterName);

	Collection<FilterDefinition> getAutoEnabledFilters();

	JdbcServices getJdbcServices();

	SqlStringGenerationContext getSqlStringGenerationContext();

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// map these to Metamodel

	@Override
	RootGraphImplementor<?> findEntityGraphByName(String name);

	/**
	 * The best guess entity name for an entity not in an association
	 */
	String bestGuessEntityName(Object object);
}
