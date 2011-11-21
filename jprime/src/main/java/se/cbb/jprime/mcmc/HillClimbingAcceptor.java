package se.cbb.jprime.mcmc;

import java.util.List;

import se.cbb.jprime.math.LogDouble;

/**
 * Hill-climbing proposal acceptor. Only accepts a proposed state x' which has higher likelihood
 * than the old state x.
 * 
 * @author Joel Sjöstrand.
 */
public class HillClimbingAcceptor implements ProposalAcceptor {

	/**
	 * Returns true if a proposed state x' has a higher likelihood than the old state x. Does not require any proposal
	 * information nor a pseudo-random number generator.
	 * @param proposedStateLikelihood the likelihood P(x').
	 * @param oldStateLikelihood the likelihood P(x).
	 * @param proposals details the proposals made for going from x to x'. May be null.
	 * @return true if suggested state accepted; false if rejected.
	 */
	@Override
	public boolean acceptProposedState(LogDouble proposedStateLikelihood,
			LogDouble oldStateLikelihood, List<Proposal> proposals) {
		// If we do have proposal information, just verify it's OK.
		if (proposals != null) {
			for (Proposal prop : proposals) {
				if (!prop.isValid()) { return false; }
			}
		}
		return proposedStateLikelihood.greaterThan(oldStateLikelihood);
	}
	
	@Override
	public String getPreInfo(String prefix) {
		return (prefix + "HILL-CLIMBING ACCEPTOR\n");
	}

	@Override
	public String getPostInfo(String prefix) {
		return null;
	}

}
