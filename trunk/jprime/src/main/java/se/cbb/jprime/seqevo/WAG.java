package se.cbb.jprime.seqevo;

/**
 * Substitution model definition.
 * TODO: More object-oriented approach (although low prio...).
 * 
 * @author Bengt Sennblad.
 * @author Lars Arvestad.
 * @author Joel Sjöstrand.
 */
public class WAG {

	/**
	 * Returns the model type described in "Whelan, S. and N. Goldman, 2001, A general empirical model of
	 * protein evolution derived from multiple protein families using
	 * a maximum likelihood approach, MBE 18:691-699.
	 * @param cacheSize matrix cache size. Probably not useful with more than twice the number
	 * of arcs in tree...?
	 * @return the model type.
	 */
	public static SubstitutionMatrixHandler createWAG(int cacheSize) {
		double[] Pi = {
				0.0866279, 0.043972, 0.0390894, 0.0570451, 0.0193078, 0.0367281, 0.0580589, 0.0832518, 0.0244313, 0.048466, 0.086209, 0.0620286, 0.0195027, 0.0384319, 0.0457631, 0.0695179, 0.0610127, 0.0143859, 0.0352742, 0.0708956
		};
		// TODO: Joel: Do these need normalization w.r.t. 1 expected event over branch length 1?
		double[] R = {
				0.551571, 0.509848, 0.738998, 1.02704, 0.908598, 1.58285, 1.41672, 0.316954, 0.193335, 0.397915, 0.906265, 0.893496, 0.210494, 1.43855, 3.37079, 2.12111, 0.113133, 0.240735, 2.00601,
				0.635346, 0.147304, 0.528191, 3.0355, 0.439157, 0.584665, 2.13715, 0.186979, 0.497671, 5.35142, 0.683162, 0.102711, 0.679489, 1.22419, 0.554413, 1.16392, 0.381533, 0.251849,
				5.42942, 0.265256, 1.54364, 0.947198, 1.12556, 3.95629, 0.554236, 0.131528, 3.01201, 0.198221, 0.0961621, 0.195081, 3.97423, 2.03006, 0.0719167, 1.086, 0.196246,
				0.0302949, 0.616783, 6.17416, 0.865584, 0.930676, 0.039437, 0.0848047, 0.479855, 0.103754, 0.0467304, 0.423984, 1.07176, 0.374866, 0.129767, 0.325711, 0.152335,
				0.0988179, 0.021352, 0.306674, 0.248972, 0.170135, 0.384287, 0.0740339, 0.390482, 0.39802, 0.109404, 1.40766, 0.512984, 0.71707, 0.543833, 1.00214,
				5.46947, 0.330052, 4.29411, 0.113917, 0.869489, 3.8949, 1.54526, 0.0999208, 0.933372, 1.02887, 0.857928, 0.215737, 0.22771, 0.301281,
				0.567717, 0.570025, 0.127395, 0.154263, 2.58443, 0.315124, 0.0811339, 0.682355, 0.704939, 0.822765, 0.156557, 0.196303, 0.588731,
				0.24941, 0.0304501, 0.0613037, 0.373558, 0.1741, 0.049931, 0.24357, 1.34182, 0.225833, 0.336983, 0.103604, 0.187247,
				0.13819, 0.499462, 0.890432, 0.404141, 0.679371, 0.696198, 0.740169, 0.473307, 0.262569, 3.87344, 0.118358,
				3.17097, 0.323832, 4.25746, 1.05947, 0.0999288, 0.31944, 1.45816, 0.212483, 0.42017, 7.8213,
				0.257555, 4.85402, 2.11517, 0.415844, 0.344739, 0.326622, 0.665309, 0.398618, 1.80034,
				0.934276, 0.088836, 0.556896, 0.96713, 1.38698, 0.137505, 0.133264, 0.305434,
				1.19063, 0.171329, 0.493905, 1.51612, 0.515706, 0.428437, 2.05845,
				0.161444, 0.545931, 0.171903, 1.52964, 6.45428, 0.649892,
				1.61328, 0.795384, 0.139405, 0.216046, 0.314887,
				4.37802, 0.523742, 0.786993, 0.232739,
				0.110864, 0.291148, 1.38823,
				2.48539, 0.365369,
				0.31473
		};

		return new SubstitutionMatrixHandler("WAG", SequenceType.AMINO_ACID, R, Pi, cacheSize);
	}
	
}