/**
 * 
 */
package org.dllearner.utilities.owl;

import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 * Remove disjunctions in disjunctions and conjunctions in conjunctions.
 * @author Lorenz Buehmann
 *
 */
public class OWLClassExpressionCleaner implements OWLClassExpressionVisitorEx<OWLClassExpression>{
	
	private OWLDataFactory df;
	
	public OWLClassExpressionCleaner(OWLDataFactory df) {
		this.df = df;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public OWLClassExpression visit(OWLClass ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectIntersectionOf)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
		Set<OWLClassExpression> operands = new TreeSet<OWLClassExpression>();
		for (OWLClassExpression operand : ce.getOperands()) {
			OWLClassExpression newOperand = operand.accept(this);
			if(newOperand instanceof OWLObjectIntersectionOf){
				operands.addAll(((OWLObjectIntersectionOf) newOperand).getOperands());
			} else {
				operands.add(newOperand);
			}
		}
		return df.getOWLObjectIntersectionOf(operands);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectUnionOf)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectUnionOf ce) {
		Set<OWLClassExpression> operands = new TreeSet<OWLClassExpression>();
		for (OWLClassExpression operand : ce.getOperands()) {
			OWLClassExpression newOperand = operand.accept(this);
			if(newOperand instanceof OWLObjectUnionOf){
				operands.addAll(((OWLObjectUnionOf) newOperand).getOperands());
			} else {
				operands.add(newOperand);
			}
		}
		return df.getOWLObjectUnionOf(operands);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectComplementOf)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectComplementOf ce) {
		OWLClassExpression result = ce;
		OWLClassExpression operand = ce.getOperand();
		if(operand.isAnonymous()){
			OWLClassExpression newOperand = operand.accept(this);
			result = df.getOWLObjectComplementOf(newOperand);
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
		OWLClassExpression result = ce;
		OWLClassExpression operand = ce.getFiller();
		if(operand.isAnonymous()){
			OWLClassExpression newOperand = operand.accept(this);
			result = df.getOWLObjectSomeValuesFrom(ce.getProperty(), newOperand);
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectAllValuesFrom)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
		OWLClassExpression result = ce;
		OWLClassExpression operand = ce.getFiller();
		if(operand.isAnonymous()){
			OWLClassExpression newOperand = operand.accept(this);
			result = df.getOWLObjectAllValuesFrom(ce.getProperty(), newOperand);
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectHasValue)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectHasValue ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectMinCardinality)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectMinCardinality ce) {
		OWLClassExpression result = ce;
		OWLClassExpression operand = ce.getFiller();
		if(operand.isAnonymous()){
			OWLClassExpression newOperand = operand.accept(this);
			result = df.getOWLObjectMinCardinality(ce.getCardinality(), ce.getProperty(), newOperand);
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectExactCardinality)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectExactCardinality ce) {
		OWLClassExpression result = ce;
		OWLClassExpression operand = ce.getFiller();
		if(operand.isAnonymous()){
			OWLClassExpression newOperand = operand.accept(this);
			result = df.getOWLObjectExactCardinality(ce.getCardinality(), ce.getProperty(), newOperand);
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectMaxCardinality)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectMaxCardinality ce) {
		OWLClassExpression result = ce;
		OWLClassExpression operand = ce.getFiller();
		if(operand.isAnonymous()){
			OWLClassExpression newOperand = operand.accept(this);
			result = df.getOWLObjectMaxCardinality(ce.getCardinality(), ce.getProperty(), newOperand);
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectHasSelf)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectHasSelf ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLObjectOneOf)
	 */
	@Override
	public OWLClassExpression visit(OWLObjectOneOf ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLDataSomeValuesFrom)
	 */
	@Override
	public OWLClassExpression visit(OWLDataSomeValuesFrom ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLDataAllValuesFrom)
	 */
	@Override
	public OWLClassExpression visit(OWLDataAllValuesFrom ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLDataHasValue)
	 */
	@Override
	public OWLClassExpression visit(OWLDataHasValue ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLDataMinCardinality)
	 */
	@Override
	public OWLClassExpression visit(OWLDataMinCardinality ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLDataExactCardinality)
	 */
	@Override
	public OWLClassExpression visit(OWLDataExactCardinality ce) {
		return ce;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx#visit(org.semanticweb.owlapi.model.OWLDataMaxCardinality)
	 */
	@Override
	public OWLClassExpression visit(OWLDataMaxCardinality ce) {
		return ce;
	}

}