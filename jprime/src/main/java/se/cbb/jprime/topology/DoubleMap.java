package se.cbb.jprime.topology;

import java.util.TreeSet;

import se.cbb.jprime.io.SampleDoubleArray;
import se.cbb.jprime.mcmc.ChangeInfo;
import se.cbb.jprime.mcmc.Dependent;
import se.cbb.jprime.mcmc.RealParameter;

/**
 * Holds a double for each vertex of a graph. No generics for the sake of speed.
 * 
 * @author Joel Sjöstrand.
 */
public class DoubleMap implements GraphMap, RealParameter {
	
	/** The name of this map, if any. */
	protected String name;
	
	/** The map values. */
	protected double[] values;
	
	/** The child dependents. */
	protected TreeSet<Dependent> dependents;
	
	/** Cache. */
	protected double[] cache = null;

	/** Details the current change. Set by a Proposer. */
	protected ChangeInfo changeInfo = null;
	
	/**
	 * Constructor. Initialises all map values to 0.0.
	 * @param name the map's name.
	 * @param size the size of the map.
	 */
	public DoubleMap(String name, int size) {
		this.name = name;
		this.values = new double[size];
		this.dependents = new TreeSet<Dependent>();
	}
	
	/**
	 * Constructor.
	 * @param name the map's name.
	 * @param size the size of the map.
	 * @param defaultVal default value for all elements.
	 */
	public DoubleMap(String name, int size, double defaultVal) {
		this(name, size);
		for (int i = 0; i < this.values.length; ++i) {
			values[i] = defaultVal;
		}
		this.dependents = new TreeSet<Dependent>();
	}
	
	/**
	 * Constructor.
	 * @param name the map's name.
	 * @param vals the initial values of this map, indexed by vertex number.
	 */
	public DoubleMap(String name, double[] vals) {
		this.name = name;
		this.values = vals;
		this.dependents = new TreeSet<Dependent>();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Object getAsObject(int x) {
		return new Double(this.values[x]);
	}

	@Override
	public void setAsObject(int x, Object value) {
		this.values[x] = ((Double) value).doubleValue();
	}

	/**
	 * Returns the element of a vertex.
	 * @param x the vertex.
	 * @return the value.
	 */
	public double get(int x) {
		return this.values[x];
	}
	
	/**
	 * Sets the element of a vertex.
	 * @param x the vertex.
	 * @param val the value.
	 */
	public void set(int x, double val) {
		this.values[x] = val;
	}

	@Override
	public int getNoOfSubParameters() {
		return this.values.length;
	}

	@Override
	public ChangeInfo getChangeInfo() {
		return this.changeInfo;
	}
	
	@Override
	public void setChangeInfo(ChangeInfo info) {
		this.changeInfo = info;
	}

	/**
	 * Caches the whole current map. May e.g. be used by a <code>Proposer</code>.
	 */
	public void cache() {
		this.cache = new double[this.values.length];
		System.arraycopy(this.values, 0, this.cache, 0, this.values.length);
	}

	/**
	 * Clears the cached map and change info. May e.g. be used by a <code>Proposer</code>.
	 */
	public void clearCache() {
		this.cache = null;
		this.changeInfo = null;
	}

	/**
	 * Replaces the current map with the cached value, and clears the latter and the change info.
	 * May e.g. be used by a <code>Proposer</code>.
	 */
	public void restoreCache() {
		this.values = this.cache;
		this.cache = null;
		this.changeInfo = null;
	}

	@Override
	public Class<?> getSampleType() {
		return SampleDoubleArray.class;
	}

	@Override
	public String getSampleHeader() {
		return this.name;
	}

	@Override
	public String getSampleValue() {
		return SampleDoubleArray.toString(this.values);
	}

	@Override
	public double getValue(int idx) {
		return this.values[idx];
	}

	@Override
	public void setValue(int idx, double value) {
		this.values[idx] = value;
	}

	@Override
	public int getSize() {
		return this.values.length;
	}

	@Override
	public Dependent[] getParentDependents() {
		return null;
	}
}
