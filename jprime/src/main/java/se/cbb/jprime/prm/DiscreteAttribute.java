package se.cbb.jprime.prm;

import se.cbb.jprime.math.IntegerInterval;

/**
 * Interface for discrete valued attributes.
 * 
 * @author Joel Sjöstrand.
 */
public interface DiscreteAttribute extends ProbabilisticAttribute {

	/**
	 * Returns the interval of this attribute.
	 * @return the interval.
	 */
	public IntegerInterval getInterval();
	
}
