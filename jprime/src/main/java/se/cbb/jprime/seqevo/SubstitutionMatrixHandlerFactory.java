package se.cbb.jprime.seqevo;

import se.cbb.jprime.io.SampleDoubleArray;

/**
 * Creates matrix handlers corresponding to various substitution models.
 * TODO: the models JC69, etc. should be moved to another subclass
 * that implements analytical solutions of the member functions.
 * <p/>
 * Note: Not a proper factory class yet, but maybe eventually.
 * 
 * @author Bengt Sennblad.
 * @author Lars Arvestad.
 * @author Joel Sjöstrand.
 */
public class SubstitutionMatrixHandlerFactory {

	/**
	 * Convenience method for creating a known substitution model from
	 * its string identifier.
	 * @param model the identifier.
	 * @param cacheSize matrix cache size. Probably not useful with more than twice the number
	 * of arcs in tree...?
	 */
	public static SubstitutionMatrixHandler create(String model, int cacheSize) {
		model = model.trim().toUpperCase();
		if (model.equals("JC69")) {
			return JC69.createJC69(cacheSize);
		} else if (model.equals("UNIFORMAA")) {
			return UniformAA.createUniformAA(cacheSize);
		} else if (model.equals("JTT")) {
			return JTT.createJTT(cacheSize);
		} else if (model.equals("UNIFORMCODON")) {
			return UniformCodon.createUniformCodon(cacheSize);
		} else if (model.equals("ARVECODON")) {
			return ArveCodon.createArveCodon(cacheSize);
		} else if (model.startsWith("USERDEFINED")) {
			// TODO: Clean-up.
			// HACK! Assumes string like "USERDEFINED=DNA;[pi1,...,pik];[r1,...,rj]".
			String seqType = model.substring(model.indexOf("=")+1, model.indexOf(";"));
			String pi = model.substring(model.indexOf(";")+1, model.indexOf(";", 22));
			String r = model.substring(model.indexOf(";", 22)+1);
			return createUserDefined(seqType,
					SampleDoubleArray.toDoubleArray(pi), SampleDoubleArray.toDoubleArray(r), cacheSize);
		} else {
			throw new IllegalArgumentException("Cannot create unknown substitution model: " + model);
		}
	};


	

	/**
	 * Returns a user-defined model type.
	 * @param seqType sequence type identifier ("DNA", "AA", "Codon").
	 * @param pi stationary frequencies (alphabet size n).
	 * @param r values of time-reversible rate matrix as if row-major, symmetric and lacking diagonal (size n*(n-1)/2).
	 * @param cacheSize matrix cache size. Probably not useful with more than twice the number
	 * of arcs in tree...?
	 * @return the model type.
	 */
	public static SubstitutionMatrixHandler createUserDefined(String seqType, double[] pi, double[] r, int cacheSize) {
		SequenceType st = SequenceType.getSequenceType(seqType);
		int dim = st.getAlphabetSize();
		int r_dim = dim * (dim - 1) / 2;
		if (pi.length != dim) {
			throw new IllegalArgumentException("Invalid size of stationary frequencies Pi: " + dim);
		} else if (r.length != r_dim) {
			throw new IllegalArgumentException("Invalid size of row-major time-reversible rate matrix R: " + r_dim);
		}
		return new SubstitutionMatrixHandler("USER-DEFINED", st, r, pi, cacheSize);
	}

}
