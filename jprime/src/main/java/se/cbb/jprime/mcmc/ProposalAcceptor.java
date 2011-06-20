package se.cbb.jprime.mcmc;

import java.util.List;

import se.cbb.jprime.math.LogDouble;

/**
 * Interface for probabilistic techniques of determining whether a suggested
 * parameter state change should be accepted or not. At the moment this
 * has been written with Metropolis-Hastings-like methods in mind, so it is
 * subject to change to become more general.
 * 
 * @author Joel Sjöstrand.
 */
public interface ProposalAcceptor extends MCMCSerializable {

	/**
	 * Returns true if a new state should be accepted.
	 * @param newStateLikelihood the likelihood P(x') of a new state x'.
	 * @param oldStateLikelihood the likelihood P(x) of the previous state x.
	 * @param proposals details the proposals made for going from x to x'.
	 * @return true if suggested state accepted; false if rejected.
	 */
	public boolean acceptNewState(LogDouble newStateLikelihood, LogDouble oldStateLikelihood,
			List<Proposal> proposals);
}
