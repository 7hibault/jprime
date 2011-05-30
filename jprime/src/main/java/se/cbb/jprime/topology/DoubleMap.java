package se.cbb.jprime.topology;

import java.util.Set;
import java.util.TreeSet;

import se.cbb.jprime.mcmc.ChangeInfo;
import se.cbb.jprime.mcmc.Dependent;
import se.cbb.jprime.mcmc.RealParameter;
import se.cbb.jprime.mcmc.SampleType;

/**
 * Holds a double for each vertex of a graph.
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

	/** Details the current change. Set by a Perturber. */
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
	 * @param graph the graph to which the map refers.
	 * @param name the map's name.
	 * @param size the size of the map.
	 * @param defaultVal default value for all elements.
	 */
	public DoubleMap(String name, int size, int defaultVal) {
		this(name, size);
		for (int i = 0; i < this.values.length; ++i) {
			values[i] = defaultVal;
		}
		this.dependents = new TreeSet<Dependent>();
	}
	
	/**
	 * Constructor.
	 * @param graph the graph to which the map refers.
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
	public void setChangeInfo(ChangeInfo info) {
		this.changeInfo = info;
	}

	@Override
	public boolean isDependentSink() {
		return this.dependents.isEmpty();
	}

	@Override
	public void addChildDependent(Dependent dep) {
		this.dependents.add(dep);
	}

	@Override
	public Set<Dependent> getChildDependents() {
		return this.dependents;
	}

	@Override
	public void cache(boolean willSample) {
		this.cache = new double[this.values.length];
		System.arraycopy(this.values, 0, this.cache, 0, this.values.length);
	}

	@Override
	public void update(boolean willSample) {
		// Change info *must* be set in case of an actual change.
		if (this.changeInfo != null) {
			for (Dependent dep : this.dependents) {
				dep.addParentChangeInfo(this.changeInfo, willSample);
			}
		}
	}

	@Override
	public void clearCache(boolean willSample) {
		this.cache = null;
		this.changeInfo = null;
	}

	@Override
	public void restoreCache(boolean willSample) {
		this.values = this.cache;
		this.cache = null;
		this.changeInfo= null;
	}

	@Override
	public void addParentChangeInfo(ChangeInfo info, boolean willSample) {
		throw new UnsupportedOperationException("DoubleMap cannot have parent dependents.");
	}

	@Override
	public SampleType getSampleType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSampleHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSampleValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getValue(int idx) {
		return this.values[idx];
	}

	@Override
	public void setValue(int idx, double value) {
		this.values[idx] = value;
	}
}
