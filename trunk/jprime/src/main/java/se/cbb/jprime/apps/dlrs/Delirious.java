package se.cbb.jprime.apps.dlrs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.template.Compound;
import org.biojava3.core.sequence.template.Sequence;

import se.cbb.jprime.io.JCommanderUsageWrapper;
import se.cbb.jprime.io.RBTreeSampleWrapper;
import se.cbb.jprime.io.SampleDoubleArray;
import se.cbb.jprime.io.SampleWriter;
import se.cbb.jprime.math.Continuous1DPDDependent;
import se.cbb.jprime.math.PRNG;
import se.cbb.jprime.mcmc.DoubleParameter;
import se.cbb.jprime.mcmc.FineProposerStatistics;
import se.cbb.jprime.mcmc.Iteration;
import se.cbb.jprime.mcmc.MCMCManager;
import se.cbb.jprime.mcmc.MultiProposerSelector;
import se.cbb.jprime.mcmc.NormalProposer;
import se.cbb.jprime.mcmc.ProposalAcceptor;
import se.cbb.jprime.mcmc.Thinner;
import se.cbb.jprime.misc.Pair;
import se.cbb.jprime.misc.Triple;
import se.cbb.jprime.seqevo.GammaSiteRateHandler;
import se.cbb.jprime.seqevo.MSAData;
import se.cbb.jprime.seqevo.SubstitutionMatrixHandler;
import se.cbb.jprime.seqevo.SubstitutionMatrixHandlerFactory;
import se.cbb.jprime.seqevo.SubstitutionModel;
import se.cbb.jprime.topology.DoubleMap;
import se.cbb.jprime.topology.GuestHostMap;
import se.cbb.jprime.topology.MPRMap;
import se.cbb.jprime.topology.NamesMap;
import se.cbb.jprime.topology.RBTree;
import se.cbb.jprime.topology.RBTreeArcDiscretiser;
import se.cbb.jprime.topology.RBTreeBranchSwapper;
import se.cbb.jprime.topology.TimesMap;

import com.beust.jcommander.JCommander;

/**
 * Java version of the Delirious application (previously known as GSR and GSRf)
 * for simultaneous reconciliation and inference
 * of a guest tree evolving inside a dated host tree. Based on the DLRS model.
 * 
 * @author Joel Sjöstrand.
 */
public class Delirious {
	
	/**
	 * Delirious starter.
	 * @param args.
	 */
	public static void main(String[] args) {
		BufferedWriter info = null;
		try {
			
			// ================ PARSE USER OPTIONS AND ARGUMENTS ================
			
			Parameters params = new Parameters();
			JCommander jc = new JCommander(params, args);
			if (args.length == 0 || params.help) {
				StringBuilder sb = new StringBuilder(65536);
				sb.append("Usage: java Delirious [options] ").append(jc.getMainParameterDescription()).append('\n');
				JCommanderUsageWrapper.getUnsortedUsage(jc, params, sb);
				System.out.println(sb.toString());
				return;
			}
			
			// ================ READ AND CREATE ALL PARAMETERS ================
			
			// MCMC chain output and auxiliary info.
			SampleWriter sampler = ParameterParser.getOut(params);
			info = ParameterParser.getInfo(params);
			info.write("=========================================================================\n");
			info.write("||                             PRE-RUN INFO                            ||\n");
			info.write("=========================================================================\n");
			info.write("DELIRIOUS\n");
			info.write("Arguments: " + Arrays.toString(args) + '\n');
			Calendar cal = Calendar.getInstance();
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			info.write("Current time: " + df.format(cal.getTime()) + '\n');
			
			// Read S and t.
			Triple<RBTree, NamesMap, TimesMap> sNamesTimes = ParameterParser.getHostTree(params, info);
			
			// Read guest-to-host leaf map.
			GuestHostMap gsMap = ParameterParser.getGSMap(params);
			
			// Substitution model first, then sequence alignment D and site rates.
			SubstitutionMatrixHandler Q = SubstitutionMatrixHandlerFactory.create(params.substitutionModel, 4 * gsMap.getNoOfLeafNames());
			LinkedHashMap<String, ? extends Sequence<? extends Compound>> sequences = ParameterParser.getMultialignment(params, Q.getSequenceType());
			MSAData D = new MSAData(Q.getSequenceType(), sequences);
			Pair<DoubleParameter, GammaSiteRateHandler> siteRates = ParameterParser.getSiteRates(params);
			
			// Pseudo-random number generator.
			PRNG prng = ParameterParser.getPRNG(params);
			
			// Read/create G and l.
			Triple<RBTree, NamesMap, DoubleMap> gNamesLengths = ParameterParser.getGuestTreeAndLengths(params, gsMap, prng, sequences, info);
			
			// Read number of iterations and thinning factor.
			Iteration iter = ParameterParser.getIteration(params);
			Thinner thinner = ParameterParser.getThinner(params, iter);
			
			// Sigma (mapping between G and S).
			MPRMap mprMap = new MPRMap(gsMap, gNamesLengths.first, gNamesLengths.second, sNamesTimes.first, sNamesTimes.second);
			
			// Read probability distribution for iid guest tree edge rates (molecular clock relaxation). 
			Triple<DoubleParameter, DoubleParameter, Continuous1DPDDependent> edgeRatePD = ParameterParser.getEdgeRatePD(params);
			
			// Create discretisation of S.
			RBTreeArcDiscretiser dtimes = ParameterParser.getDiscretizer(params, sNamesTimes.first, sNamesTimes.third, gNamesLengths.first);
			
			// Duplication-loss probabilities over discretised S.
			Triple<DoubleParameter, DoubleParameter, DupLossProbs> dupLoss = ParameterParser.getDupLossProbs(params, mprMap, sNamesTimes.first, gNamesLengths.first, dtimes);
			
			// ================ CREATE MODELS, PROPOSERS, ETC. ================
			
			// Substitution model. NOTE: Root arc is turned on!!!!
			SubstitutionModel sm = new SubstitutionModel("SubstitutionModel", D, siteRates.second, Q, gNamesLengths.first, gNamesLengths.second, gNamesLengths.third, true);
			
			// DLRS model.
			DLRSModel dlrs = new DLRSModel(gNamesLengths.first, sNamesTimes.first, mprMap, gNamesLengths.third, dtimes, dupLoss.third, edgeRatePD.third);
			
			// Proposers.
			NormalProposer dupRateProposer = ParameterParser.getNormalProposer(params, dupLoss.first, iter, prng, params.tuningDupRate);
			NormalProposer lossRateProposer = ParameterParser.getNormalProposer(params, dupLoss.second, iter, prng, params.tuningLossRate);
			NormalProposer edgeRateMeanProposer = ParameterParser.getNormalProposer(params, edgeRatePD.first, iter, prng, params.tuningEdgeRateMean);
			NormalProposer edgeRateCVProposer = ParameterParser.getNormalProposer(params, edgeRatePD.second, iter, prng, params.tuningEdgeRateCV);
			NormalProposer siteRateShapeProposer = ParameterParser.getNormalProposer(params, siteRates.first, iter, prng, params.tuningSiteRateShape);
			RBTreeBranchSwapper guestTreeProposer = ParameterParser.getBranchSwapper(gNamesLengths.first, gNamesLengths.third, iter, prng);
			double[] moves = SampleDoubleArray.toDoubleArray(params.tuningGuestTreeMoveWeights);
			guestTreeProposer.setOperationWeights(moves[0], moves[1], moves[2]);
			NormalProposer lengthsProposer = ParameterParser.getNormalProposer(params, gNamesLengths.third, iter, prng, params.tuningLengths);
			double[] lengthsWeights = SampleDoubleArray.toDoubleArray(params.tuningLengthsSelectorWeights);
			lengthsProposer.setSubParameterWeights(lengthsWeights);
			
			// Proposer selector.
			MultiProposerSelector selector = ParameterParser.getSelector(params, prng);
			selector.add(dupRateProposer, ParameterParser.getProposerWeight(params.tuningWeightDupRate, iter));
			selector.add(lossRateProposer, ParameterParser.getProposerWeight(params.tuningWeightLossRate, iter));
			selector.add(edgeRateMeanProposer, ParameterParser.getProposerWeight(params.tuningWeightEdgeRateMean, iter));
			selector.add(edgeRateCVProposer, ParameterParser.getProposerWeight(params.tuningWeightEdgeRateCV, iter));
			selector.add(siteRateShapeProposer, ParameterParser.getProposerWeight(params.tuningWeightSiteRateShape, iter));
			selector.add(guestTreeProposer, ParameterParser.getProposerWeight(params.tuningWeightG, iter));
			selector.add(lengthsProposer, ParameterParser.getProposerWeight(params.tuningWeightLengths, iter));
			
			// Inactivate fixed proposers.
			if (params.dupRate != null        && params.dupRate.matches("FIXED|Fixed|fixed"))        { dupRateProposer.setEnabled(false); }
			if (params.lossRate != null       && params.lossRate.matches("FIXED|Fixed|fixed"))       { lossRateProposer.setEnabled(false); }
			if (params.edgeRatePDMean != null && params.edgeRatePDMean.matches("FIXED|Fixed|fixed")) { edgeRateMeanProposer.setEnabled(false); }
			if (params.edgeRatePDCV != null   && params.edgeRatePDCV.matches("FIXED|Fixed|fixed"))   { edgeRateCVProposer.setEnabled(false); }
			if (params.siteRateCats == 1      || params.siteRateShape.matches("FIXED|Fixed|fixed"))  { siteRateShapeProposer.setEnabled(false); }
			if (params.guestTreeFixed)                                                               { guestTreeProposer.setEnabled(false); }
			if (params.lengthsFixed)                                                                 { lengthsProposer.setEnabled(false); }
			
			// Proposal acceptor.
			ProposalAcceptor acceptor = ParameterParser.getAcceptor(params, prng);
			
			// Overall statistics.
			FineProposerStatistics stats = new FineProposerStatistics(iter, 8);
			
			// ================ SETUP MCMC HIERARCHY ================
			
			MCMCManager manager = new MCMCManager(iter, thinner, selector, acceptor, sampler, prng, stats);
			manager.setDebugMode(params.debug);
			
			manager.addModel(sm);
			manager.addModel(dlrs);
			
			manager.addSampleable(iter);
			manager.addSampleable(manager);			// Overall likelihood.
			manager.addSampleable(sm);
			manager.addSampleable(dlrs);
			manager.addSampleable(dupLoss.first);
			manager.addSampleable(dupLoss.second);
			manager.addSampleable(edgeRatePD.first);
			manager.addSampleable(edgeRatePD.second);
			if (siteRateShapeProposer.isEnabled()) {
				manager.addSampleable(siteRates.first);
			}
			manager.addSampleable(new RBTreeSampleWrapper(gNamesLengths.first, gNamesLengths.second));
			if (params.outputLengths) {
				manager.addSampleable(new RBTreeSampleWrapper(gNamesLengths.first, gNamesLengths.second, gNamesLengths.third));
			}
			
			// ================ WRITE PRE-INFO ================
			String prefix = "\t";
			info.write(manager.getPreInfo(prefix));
			info.flush();   // Don't close, maybe using stdout for both sampling and info...
			
			// ================ RUN ================
			manager.run();
			
			// ================ WRITE POST-INFO ================
			info.write("=========================================================================\n");
			info.write("||                             POST-RUN INFO                           ||\n");
			info.write("=========================================================================\n");
			info.write("DELIRIOUS\n");
			info.write(manager.getPostInfo(prefix));
			info.flush();
			sampler.close();
			info.close();
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.print("\nUse option -h or --help to show usage.");
			if (info != null) {
				Writer w = new StringWriter();
			    PrintWriter pw = new PrintWriter(w);
			    e.printStackTrace(pw);
				try {
					info.write("Run failed. Reason:\n" + w.toString());
					info.close();
					pw.close();
					w.close();
				} catch (IOException f) {
				}
			}
		}
	}
	
	
}