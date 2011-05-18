package framework

import fieldml.evaluator.Evaluator
import fieldml.evaluator.ArgumentEvaluator
import fieldml.evaluator.ParameterEvaluator
import fieldml.evaluator.PiecewiseEvaluator
import fieldml.evaluator.ReferenceEvaluator
import fieldml.evaluator.SubtypeEvaluator

package object valuesource
{
    implicit def valueSource( evaluator : Evaluator ) : ValueSource = evaluator.asInstanceOf[ValueSource]
}
