package se.cbb.jprime.topology;

import java.util.Map;

import se.cbb.jprime.mcmc.ChangeInfo;
import se.cbb.jprime.mcmc.Dependent;
import se.cbb.jprime.mcmc.ProperDependent;

/**
 * Extends a guest-host map with functionality concerning the most parsimonious reconciliation
 * when embedding the guest tree in the host tree using duplication and loss events.
 * It is assumed that the guest-to-host leaf mapping does not change (nor the names), while the
 * guest or host topologies may. See also <code>LeafMap</code>.
 * <p/>
 * Note: Only bifurcating trees supported at the moment.
 * 
 * @author Joel Sjöstrand.
 */
public class MPRMap implements ProperDependent {

	/** Guest-to-host leaf map. */
	private GuestHostMap GSMap;
	
	/** Guest tree topology. */
	private RootedBifurcatingTreeParameter G;
	
	/** Host tree topology. */
	private RootedBifurcatingTreeParameter S;
	
	/** Child-vertex-to-host-vertex MPR map. */
	private int[] sigma;
	
	/** Cache. */
	private int[] sigmaCache = null;
	
	/**
	 * Constructor.
	 * @param GSMap guest-to-host leaf map.
	 * @param G guest tree topology.
	 * @param GNames guest tree leaf names.
	 * @param S host tree topology.
	 * @param SNames host tree leaf names.
	 */
	public MPRMap(GuestHostMap GSMap, RootedBifurcatingTreeParameter G, NamesMap GNames,
			RootedBifurcatingTreeParameter S, NamesMap SNames) {
		this.GSMap = GSMap;
		this.G = G;
		this.S = S;
		this.sigma = new int[G.getNoOfVertices()];
		
		// Fill the sigma map for the leaves only once, prior to the rest.
		for (int l : this.G.getLeaves()) {
			String sigmaname = this.GSMap.getHostLeafName(GNames.get(l));
			this.sigma[l] = SNames.getVertex(sigmaname);
		}
		
		// Now fill the rest.
		this.computeSigma(this.G.getRoot());
	}
	
	/**
	 * Copy constructor.
	 * @param map the map to copy.
	 */
	public MPRMap(MPRMap map) {
		this.GSMap = map.GSMap;
		this.G = map.G;
		this.S = map.S;
		this.sigma = new int[map.sigma.length];
		System.arraycopy(map.sigma, 0, this.sigma, 0, map.sigma.length);
	}

	@Override
	public Dependent[] getParentDependents() {
		return new Dependent[] { this.S, this.G };
	}

	@Override
	public void cacheAndUpdate(Map<Dependent, ChangeInfo> changeInfos,
			boolean willSample) {
		if (changeInfos.get(this.G) != null || changeInfos.get(this.S) != null) {
			// Full cache and update regardless of children's changes.
			this.sigmaCache = new int[this.sigma.length];
			System.arraycopy(this.sigma, 0, this.sigmaCache, 0, this.sigma.length);
			this.computeSigma(this.G.getRoot());
			changeInfos.put(this, new ChangeInfo(this, "Updated MPR map (i.e. sigma map)."));
		} else {
			changeInfos.put(this, null);
		}
	}

	@Override
	public void clearCache(boolean willSample) {
		this.sigmaCache = null;
	}

	@Override
	public void restoreCache(boolean willSample) {
		this.sigma = this.sigmaCache;
		this.sigmaCache = null;
	}
	
	/**
	 * Recursive method for determining the sigma mapping of the subtree of G rooted at x.
	 * Fills the sigma array and also returns sigma(x).
	 * @param x the vertex of G.
	 */
	private int computeSigma(int x) {
		if (this.G.isLeaf(x)) {
			// Assumed to be correct already.
			return (this.sigma[x]);
		}
		int lcSigma = this.computeSigma(this.G.getLeftChild(x));
		int rcSigma = this.computeSigma(this.G.getRightChild(x));
		sigma[x] = this.S.getLCA(lcSigma, rcSigma);
		return sigma[x];
	}

	/**
	 * For x in V(G), returns the lowermost vertex/arc v in V(S) where x may be placed
	 * when reconciling G and S using only duplications and losses. More formally,
	 * sigma is a function f: V(G) -> V(S) where
	 * <ol>
	 * <li>sigma(x) is the corresponding leaf in V(S) when x is a leaf.</li>
	 * <li>sigma(x) = LCA_{c in children(x)}(sigma(c)) otherwise.</li>
	 * </ol>
	 * @param x the vertex of G.
	 * @return sigma(x).
	 */
	public int getSigma(int x) {
		return this.sigma[x];
	}
	
	/**
	 * Returns true if a vertex x of G is forced to be a duplication, i.e., it sigma(x)==sigma(y) or sigma(x)==sigma(z),
	 * where y and z are its children.
	 * @param x the vertex of x.
	 * @return true if x must be a duplication in the MPR; false otherwise.
	 */
	public boolean isDuplication(int x) {
		if (this.G.isLeaf(x)) {
			return false;
		}
		return (this.sigma[x] == this.sigma[this.G.getLeftChild(x)] || this.sigma[x] == this.sigma[this.G.getRightChild(x)]);
	}

	@Override
	public boolean isProperDependent() {
		return true;
	}
	
}