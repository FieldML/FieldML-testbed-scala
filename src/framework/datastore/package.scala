package framework

import fieldml.evaluator.ParameterEvaluator

package object datastore
{
    implicit def parameterEvaluatorApplier( evaluator : ParameterEvaluator ) = new ParameterEvaluatorApplier( evaluator )
}
