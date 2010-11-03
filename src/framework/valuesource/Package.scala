package framework

import fieldml.evaluator.Evaluator
import fieldml.evaluator.AbstractEvaluator
import fieldml.evaluator.ParameterEvaluator
import fieldml.evaluator.PiecewiseEvaluator
import fieldml.evaluator.ReferenceEvaluator
import fieldml.evaluator.SubtypeEvaluator

package object valuesource
{
    implicit def valueSource( evaluator : Evaluator ) : ValueSource = evaluator.asInstanceOf[ValueSource]
    
    /*
    implicit def abstractEvaluatorValueSource( evaluator : AbstractEvaluator ) : AbstractEvaluatorValueSource = evaluator.asInstanceOf[AbstractEvaluatorValueSource]

    implicit def functionEvaluatorValueSource( evaluator : FunctionEvaluator ) : FunctionEvaluatorValueSource = evaluator.asInstanceOf[FunctionEvaluatorValueSource]

    implicit def parameterEvaluatorValueSource( evaluator : ParameterEvaluator ) : ParameterEvaluatorValueSource = evaluator.asInstanceOf[ParameterEvaluatorValueSource]

    implicit def piecewiseEvaluatorValueSource( evaluator : PiecewiseEvaluator ) : PiecewiseEvaluatorValueSource = evaluator.asInstanceOf[PiecewiseEvaluatorValueSource]

    implicit def referenceEvaluatorValueSource( evaluator : ReferenceEvaluator ) : ReferenceEvaluatorValueSource = evaluator.asInstanceOf[ReferenceEvaluatorValueSource]

    implicit def subtypeEvaluatorValueSource( evaluator : SubtypeEvaluator ) : SubtypeEvaluatorValueSource = evaluator.asInstanceOf[SubtypeEvaluatorValueSource]
    */
}
