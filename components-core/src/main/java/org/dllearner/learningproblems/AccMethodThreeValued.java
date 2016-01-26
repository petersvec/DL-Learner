package org.dllearner.learningproblems;

/**
 * Created by ailin on 16-1-26.
 */
public interface AccMethodThreeValued extends AccMethod {
	/**
	 * Calculate generalised measures according to method in A Note on the Evaluation of Inductive Concept Classification Procedures
	 * @param pos1 nr of instances matching the concept and in I_C
	 * @param neg1 nr of instances matching the negated concept and in D_C
	 * @param icPos nr of total instances matching the concept
	 * @param icNeg nr of total instances matching the negated concept
	 * @param posEx nr of instances in the target concept
	 * @param negatedPosEx nr of instances in the negated target concept
	 * @param noise noise parameter
	 * @return
	 */
	double getAccOrTooWeak3(int pos1, int neg1, int icPos, int icNeg, int posEx, int negatedPosEx, double noise);
}