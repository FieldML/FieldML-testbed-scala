package framework.valuesource

import fieldml.evaluator.Evaluator
import fieldml.valueType.ContinuousType

import framework.value.Value
import framework.ContinuousFunctionEvaluator
import framework.EvaluationState

class ContinuousFunctionEvaluatorValueSource( name : String, function : ( Array[Double], Array[Double] ) => Array[Double], var1 : Evaluator, var2 : Evaluator, valueType : ContinuousType )
    extends ContinuousFunctionEvaluator( name, function, var1, var2, valueType )
    with ValueSource
{
}
