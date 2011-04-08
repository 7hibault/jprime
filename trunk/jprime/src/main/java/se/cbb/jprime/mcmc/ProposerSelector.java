package se.cbb.jprime.mcmc;

import java.util.List;
import java.util.Set;

/**
 * Interface for selecting one or more MCMC proposers for actual
 * perturbation based on their current weights. Implementing classes may e.g. consider:
 * <ul>
 * <li>Selecting one sole proposer uniformly.</li>
 * <li>Selecting multiple proposers uniformly.</li>
 * <li>Selecting the next proposer in a round-robin manner.</li>
 * <li>Selecting the next proposer conditioned on the previous ones.</li>
 * <li>...</li>
 * </ul>
 *  
 * @author Joel Sjöstrand.
 */
public interface ProposerSelector extends MCMCSerializable {

	/**
	 * Selects a subset of a list of proposers which can then be used
	 * for actual perturbations. It is up to implementing classes to
	 * decide whether they e.g. return only a single object or multiple
	 * objects. Furthermore, the returned proposers must be guaranteed to
	 * be acting on disjoint sets of state parameters.
	 * @param proposers the set of proposers to choose among.
	 * @return a subset of proposers, no pairs acting on the same state parameter.
	 */
	public Set<Proposer> getDisjointProposers(List<Proposer> proposers);
}
