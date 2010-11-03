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
        
        for(
            key <- index.asInstanceOf[ValueSource].evaluate( state );
            eval <- delegations.get( key.eValue );
            v <- eval.evaluate( state )
            )
        {
            state.pop()
            return Some(v)
        }

        state.pop()
        return None
    }

}
