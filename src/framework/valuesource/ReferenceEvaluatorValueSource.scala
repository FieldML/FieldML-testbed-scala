package framework.valuesource

import fieldml.evaluator.ReferenceEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ReferenceEvaluatorValueSource( name : String, refEvaluator : Evaluator ) 
    extends ReferenceEvaluator( name, refEvaluator )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        state.pushAndApply( name, binds.toSeq )
        
        val v = refEvaluator.evaluate( state )
        
        state.pop()
        
        return v
    }
}
