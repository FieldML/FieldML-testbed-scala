package framework.valuesource

import fieldml.evaluator.PiecewiseEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import framework.value.Value
import framework.Context
import framework.EvaluationState

class PiecewiseEvaluatorValueSource( name : String, valueType : ValueType, index : Evaluator )
    extends PiecewiseEvaluator( name, valueType, index )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        state.pushAndApply( binds.toSeq )
        
        val value = for(
            key <- index.evaluate( state );
            eval <- delegations.get( key.eValue );
            v <- eval.evaluate( state )
            ) yield v

        state.pop()
        return value
    }

}
