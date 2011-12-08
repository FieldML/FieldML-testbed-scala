package framework.valuesource

import fieldml.evaluator.Evaluator
import fieldml.valueType.EnsembleType

import framework.value.Value
import framework.EnsembleFunctionEvaluator
import framework.EvaluationState

class EnsembleFunctionEvaluatorValueSource( name : String, function : ( Array[Double] ) => Int, var1 : Evaluator, valueType : EnsembleType )
    extends EnsembleFunctionEvaluator( name, function, var1, valueType )
    with ValueSource
{
}
