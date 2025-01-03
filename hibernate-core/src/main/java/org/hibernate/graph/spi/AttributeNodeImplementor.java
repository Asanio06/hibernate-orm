/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.graph.spi;

import org.hibernate.graph.AttributeNode;
import org.hibernate.metamodel.model.domain.ManagedDomainType;

import java.util.Map;


/**
 * Integration version of the {@link AttributeNode} contract
 *
 * @author Strong Liu
 * @author Steve Ebersole
 */
public interface AttributeNodeImplementor<J> extends AttributeNode<J>, GraphNodeImplementor<J> {

	@Override
	AttributeNodeImplementor<J> makeCopy(boolean mutable);

	@Override
	SubGraphImplementor<?> makeSubGraph();

	@Override
	SubGraphImplementor<?> makeKeySubGraph();

	@Override
	<S> SubGraphImplementor<S> makeSubGraph(Class<S> subtype);

	@Override
	<S> SubGraphImplementor<S> makeKeySubGraph(Class<S> subtype);

	@Override
	<S> SubGraphImplementor<S> makeSubGraph(ManagedDomainType<S> subtype);

	@Override
	<S> SubGraphImplementor<S> makeKeySubGraph(ManagedDomainType<S> subtype);

	void merge(AttributeNodeImplementor<J> other);

	SubGraphImplementor<?> getSubGraph();

	SubGraphImplementor<?> getKeySubGraph();

	@Override
	Map<Class<?>, SubGraphImplementor<?>> getSubGraphs();

	@Override
	Map<Class<?>, SubGraphImplementor<?>> getKeySubGraphs();
}
