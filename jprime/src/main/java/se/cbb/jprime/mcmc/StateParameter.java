package se.cbb.jprime.mcmc;

/**
 * Interface for parameters, e.g. the states of an MCMC chain.
 * A parameter may e.g. be a vector or matrix, in which the individual
 * elements are referred to as "sub-parameters".
 * <p/>
 * A parameter is also a <code>Dependent</code>, although it is generally assumed that it is
 * a source in the corresponding dependency DAG. Even though there may be interconnections between
 * parameters (e.g. the times t of a tree S), it is often assumed that these are independent
 * (although a Proposer which changes S usually also perturbs t). 
 *  
 * @author Joel Sjöstrand.
 */
public interface StateParameter extends Dependent, Sampleable, MCMCSerializable {

	/**
	 * Returns the name of the parameter.
	 * @return the name.
	 */
	public String getName();
	
	/**
	 * Returns the number of sub-parameters, e.g. the number of 
	 * elements if the parameter is a vector. For scalars,
	 * 1 should be returned.
	 * @return the number of sub-parameters; 1 for scalar parameters.
	 */
	public int getNoOfSubParameters();
	
}
