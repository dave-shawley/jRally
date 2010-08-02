package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import standup.HasObservableState;
import standup.StateObserver;

class TestObserver<ObservedType,PropertyType>
	implements StateObserver<ObservedType,PropertyType>
{
	private final List<PropertyType> expectedValues;
	private final Iterator<PropertyType> nextValue;
	private PropertyType currentValue;

	public TestObserver(PropertyType initialValue, PropertyType... propertyTypes) {
		this.currentValue = initialValue;
		this.expectedValues = new ArrayList<PropertyType>(propertyTypes.length);
		CollectionUtils.addAll(this.expectedValues, propertyTypes);
		this.nextValue = this.expectedValues.iterator();
	}

	public void update(HasObservableState<ObservedType,PropertyType> obs,
			           PropertyType oldValue, PropertyType newValue)
	{
		assertTrue(this.nextValue.hasNext());
		assertEquals(currentValue, obs.getState());
		assertEquals(currentValue, oldValue);
		currentValue = this.nextValue.next();
		assertEquals(currentValue, newValue);
	}

	public boolean isFinished() {
		return !this.nextValue.hasNext();
	}

}
