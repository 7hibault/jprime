package se.cbb.jprime.topology;

/**
 * Specialisation constraining a rooted tree to be bifurcating, i.e.
 * interior vertices and the root have out-degree 2, whereas leaves
 * have out-degree 0. See the super-interface for more details.
 * 
 * @author Joel Sjöstrand.
 */
public interface RootedBifurcatingTree extends RootedTree {

	/**
	 * Returns the left child of a vertex x.
	 * If x is a leaf, NULL (as defined in the super-interface) is returned.
	 * @param x the vertex.
	 * @return the left child of x.
	 */
	public int getLeftChild(int x);
	
	/**
	 * Returns the right child of a vertex x.
	 * If x is a leaf, NULL (as defined in the super-interface) is returned.
	 * @param x the vertex.
	 * @return the right child of x.
	 */
	public int getRightChild(int x);
	
}
