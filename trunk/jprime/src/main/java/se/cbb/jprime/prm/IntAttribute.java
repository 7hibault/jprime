package se.cbb.jprime.prm;

import java.util.ArrayList;
import java.util.Random;

import se.cbb.jprime.math.IntegerInterval;

/**
 * Defines an integer attribute, either bounded or unbounded.
 * 
 * @author Joel Sjöstrand.
 */
public class IntAttribute implements DiscreteAttribute {
	
	/** PRM class. */
	private PRMClass prmClass;
	
	/** Attribute name. */
	private String name;
	
	/** Entities. */
	private ArrayList<Integer> entities;
	
	/** Dependency constraints */
	private DependencyConstraints dependencyConstraints;
	
	/** Interval defining valid range. */
	IntegerInterval interval;
	
	/**
	 * Constructor for bounded or unbounded integer range.
	 * @param prmClass PRM class this attribute belongs to.
	 * @param name attribute's name. Should be unique within PRM class.
	 * @param initialCapacity initial capacity for attribute entities.
	 * @param dependencyConstraints dependency structure constraints.
	 * @param interval the valid range of values.
	 */
	public IntAttribute(PRMClass prmClass, String name, int initialCapacity,
			DependencyConstraints dependencyConstraints, IntegerInterval interval) {
		this.prmClass = prmClass;
		this.name = name;
		this.entities = new ArrayList<Integer>(initialCapacity);
		this.dependencyConstraints = dependencyConstraints;
		this.interval = interval;
		this.prmClass.addProbAttribute(this);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public DataType getDataType() {
		return ProbabilisticAttribute.DataType.DISCRETE;
	}

	@Override
	public DependencyConstraints getDependencyConstraints() {
		return this.dependencyConstraints;
	}

	@Override
	public IntegerInterval getInterval() {
		return this.interval;
	}

	@Override
	public Object getRandomEntityAsObject(Random random) {
		return new Integer(this.interval.getRandom(random));
	}

	@Override
	public PRMClass getPRMClass() {
		return this.prmClass;
	}

	@Override
	public Object getEntityAsObject(int idx) {
		return entities.get(idx);
	}

	@Override
	public void setEntityAsObject(int idx, Object entity) {
		this.entities.set(idx, (Integer) entity);
	}

	@Override
	public void addEntityAsObject(Object entity) {
		this.entities.add((Integer) entity);
	}
	
	/**
	 * Returns an attribute value.
	 * @param idx the index.
	 * @return the value.
	 */
	public int getEntity(int idx) {
		return this.entities.get(idx).intValue();
	}
	
	/**
	 * Sets an attribute value.
	 * @param idx the index.
	 * @param value the value.
	 */
	public void setEntity(int idx, int value) {
		this.entities.set(idx, new Integer(value));
	}
	
	/**
	 * Adds an attribute value.
	 * @param value the value.
	 */
	public void addEntity(int value) {
		this.entities.add(new Integer(value));
	}
}
