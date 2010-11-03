package framework.valuesource

import fieldml.evaluator.ReferenceEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ReferenceEvaluatorValueSource( name : String, valueDomain : ValueType, refEvaluator : Evaluator ) 
    extends ReferenceEvaluator( name, valueDomain, refEvaluator )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        state.pushAndApply( binds.toSeq )
        
        val v = refEvaluator.evaluate( state )

        state.pop()
        
        return v
    }
}
