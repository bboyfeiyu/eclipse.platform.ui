/*******************************************************************************
 * Copyright (c) 2010 Ovidio Mallo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ovidio Mallo - initial API and implementation (bug 305367)
 ******************************************************************************/

package org.eclipse.core.tests.internal.databinding.observable.masterdetail;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.internal.databinding.observable.masterdetail.MapDetailValueObservableMap;
import org.eclipse.jface.databinding.conformance.util.MapChangeEventTracker;
import org.eclipse.jface.examples.databinding.model.SimplePerson;
import org.eclipse.jface.tests.databinding.AbstractDefaultRealmTestCase;

/**
 * @since 1.3
 */
public class MapDetailValueObservableMapTest extends
		AbstractDefaultRealmTestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				MapDetailValueObservableMapTest.class.getName());
		suite.addTestSuite(MapDetailValueObservableMapTest.class);
		return suite;
	}

	public void testGetKeyType() {
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				new WritableMap(SimplePerson.class, SimplePerson.class),
				BeansObservables.valueFactory("name"), String.class);

		assertSame(SimplePerson.class, mdom.getKeyType());
	}

	public void testGetValueType() {
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				new WritableMap(), BeansObservables.valueFactory("name"),
				String.class);

		assertSame(String.class, mdom.getValueType());
	}

	public void testGetObserved() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// The observed object is the master key set.
		assertSame(masterMap, mdom.getObserved());
	}

	public void testMasterSetInitiallyNotEmpty() {
		WritableMap masterMap = new WritableMap();
		SimplePerson person = new SimplePerson();
		person.setName("name");
		masterMap.put(person, person);
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Make sure that a non-empty master key set is initialized correctly.
		assertEquals(masterMap.size(), mdom.size());
		assertEquals(person.getName(), mdom.get(person));
	}

	public void testAddRemove() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Initially, the detail map is empty.
		assertTrue(mdom.isEmpty());

		// Add a first person and check that its name is in the detail map.
		SimplePerson p1 = new SimplePerson();
		p1.setName("name1");
		masterMap.put(p1, p1);
		assertEquals(masterMap.size(), mdom.size());
		assertEquals(p1.getName(), mdom.get(p1));

		// Add a second person and check that it's name is in the detail map.
		SimplePerson p2 = new SimplePerson();
		p2.setName("name2");
		masterMap.put(p2, p2);
		assertEquals(masterMap.size(), mdom.size());
		assertEquals(p2.getName(), mdom.get(p2));

		// Remove the first person from the master map and check that we still
		// have the name of the second person in the detail map.
		masterMap.remove(p1);
		assertEquals(masterMap.size(), mdom.size());
		assertEquals(p2.getName(), mdom.get(p2));

		// Remove the second person as well.
		masterMap.remove(p2);
		assertTrue(mdom.isEmpty());
	}

	public void testChangeDetail() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Change the detail attribute explicitly.
		SimplePerson p1 = new SimplePerson();
		p1.setName("name1");
		masterMap.put(p1, p1);
		assertEquals(p1.getName(), mdom.get(p1));
		p1.setName("name2");
		assertEquals(p1.getName(), mdom.get(p1));

		// Change the detail attribute by changing the master.
		SimplePerson p2 = new SimplePerson();
		p2.setName("name3");
		masterMap.put(p1, p2);
		assertEquals(p2.getName(), mdom.get(p1));
	}

	public void testPut() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Change the detail attribute explicitly.
		SimplePerson person = new SimplePerson();
		person.setName("name1");
		masterMap.put(person, person);
		assertEquals(person.getName(), mdom.get(person));

		// Set a new name on the detail map.
		mdom.put(person, "name2");
		// Check that the name has been propagated to the master.
		assertEquals("name2", person.getName());
		assertEquals(person.getName(), mdom.get(person));
	}

	public void testContainsValue() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Add a person with a given name.
		SimplePerson person = new SimplePerson();
		person.setName("name");
		masterMap.put(person, person);

		// Make sure the name of the person is contained.
		assertTrue(mdom.containsValue(person.getName()));

		// Remove the person and make sure that it's name cannot be found
		// anymore.
		masterMap.remove(person);
		assertFalse(mdom.containsValue(person.getName()));
	}

	public void testRemove() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Add two person objects to the map.
		SimplePerson p1 = new SimplePerson();
		SimplePerson p2 = new SimplePerson();
		masterMap.put(p1, p1);
		masterMap.put(p2, p2);

		// Initially, both person objects should be contained in the detail map.
		assertTrue(mdom.containsKey(p1));
		assertTrue(mdom.containsKey(p2));

		// Remove one person and check that it is not contained anymore.
		mdom.remove(p1);
		assertFalse(mdom.containsKey(p1));
		assertTrue(mdom.containsKey(p2));

		// Trying to remove a non-existent is allowed but has no effect.
		mdom.remove(p1);
		assertFalse(mdom.containsKey(p1));
		assertTrue(mdom.containsKey(p2));
	}

	public void testDetailObservableChangeEvent() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		MapChangeEventTracker changeTracker = MapChangeEventTracker
				.observe(mdom);

		SimplePerson person = new SimplePerson();
		person.setName("old name");

		// Initially, we should not have received any event.
		assertEquals(0, changeTracker.count);

		// Add the person and check that we receive an addition event on the
		// correct index and with the correct value.
		masterMap.put(person, person);
		assertEquals(1, changeTracker.count);
		assertEquals(1, changeTracker.event.diff.getAddedKeys().size());
		assertEquals(0, changeTracker.event.diff.getRemovedKeys().size());
		assertEquals(0, changeTracker.event.diff.getChangedKeys().size());
		assertSame(person, changeTracker.event.diff.getAddedKeys().iterator()
				.next());
		assertNull(changeTracker.event.diff.getOldValue(person));
		assertEquals("old name", changeTracker.event.diff.getNewValue(person));

		// Change the detail property and check that we receive a replace
		person.setName("new name");
		assertEquals(2, changeTracker.count);
		assertEquals(0, changeTracker.event.diff.getAddedKeys().size());
		assertEquals(0, changeTracker.event.diff.getRemovedKeys().size());
		assertEquals(1, changeTracker.event.diff.getChangedKeys().size());
		assertSame(person, changeTracker.event.diff.getChangedKeys().iterator()
				.next());
		assertEquals("old name", changeTracker.event.diff.getOldValue(person));
		assertEquals("new name", changeTracker.event.diff.getNewValue(person));
	}

	public void testMasterNull() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Make sure null values are handled gracefully.
		masterMap.put(null, null);
		assertEquals(1, mdom.size());
		assertNull(mdom.get(null));
	}

	public void testDetailObservableValuesAreDisposed() {
		final Map detailObservables = new HashMap();
		IObservableFactory detailValueFactory = new IObservableFactory() {
			public IObservable createObservable(Object target) {
				WritableValue detailObservable = new WritableValue();
				// Remember the created observables.
				detailObservables.put(target, detailObservable);
				return detailObservable;
			}
		};

		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, detailValueFactory, null);

		Object master1 = new Object();
		Object master2 = new Object();
		masterMap.put(master1, master1);
		masterMap.put(master2, master2);

		// Attach a listener in order to ensure that all detail observables are
		// actually created.
		MapChangeEventTracker.observe(mdom);

		assertEquals(mdom.size(), detailObservables.size());

		// No detail observables should be disposed yet.
		assertFalse(((WritableValue) detailObservables.get(master1))
				.isDisposed());
		assertFalse(((WritableValue) detailObservables.get(master2))
				.isDisposed());

		// Only the detail observable for the removed master should be disposed.
		masterMap.remove(master2);
		assertFalse(((WritableValue) detailObservables.get(master1))
				.isDisposed());
		assertTrue(((WritableValue) detailObservables.get(master2))
				.isDisposed());

		// After disposing the detail map, all detail observables should be
		// disposed.
		mdom.dispose();
		assertTrue(((WritableValue) detailObservables.get(master1))
				.isDisposed());
		assertTrue(((WritableValue) detailObservables.get(master2))
				.isDisposed());
	}

	public void testDisposeOnMasterDisposed() {
		WritableMap masterMap = new WritableMap();
		MapDetailValueObservableMap mdom = new MapDetailValueObservableMap(
				masterMap, BeansObservables.valueFactory("name"), String.class);

		// Initially, nothing should be disposed.
		assertFalse(masterMap.isDisposed());
		assertFalse(mdom.isDisposed());

		// Upon disposing the master map, the detail map should be disposed as
		// well.
		masterMap.dispose();
		assertTrue(masterMap.isDisposed());
		assertTrue(mdom.isDisposed());
	}
}
