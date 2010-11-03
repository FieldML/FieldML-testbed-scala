package framework.valuesource

import fieldml.evaluator.AbstractEvaluator
import fieldml.valueType.ContinuousType

import framework.value.Value
import framework.FunctionEvaluator
import framework.EvaluationState

class FunctionEvaluatorValueSource( name : String, function : ( Array[Double], Array[Double] ) => Array[Double], var1 : AbstractEvaluator, var2 : AbstractEvaluator, valueType : ContinuousType )
    extends FunctionEvaluator( name, function, var1, var2, valueType )
    with ValueSource
{
}
