package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType.ValueType
import fieldml.FieldmlObject
import fieldml.evaluator.Evaluator
import fieldml.evaluator.ArgumentEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ArgumentEvaluatorValueSource( name : String, valueType : ValueType, val explicitVariables : ArgumentEvaluator* )
    extends ArgumentEvaluator( name, valueType, explicitVariables:_* )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        if( state.getBind( this ) == None )
        {
            return None
        }
        
        val argumentBinds = explicitVariables.flatMap( x => Some( Tuple2[Evaluator, Evaluator]( x, new ConstantValueSource( x.evaluate( state ).get ) ) ) )
        
        val tempState = state.restart( name, this, argumentBinds )
        
        tempState.getBind( this ).flatMap( _.evaluate( tempState ) )
    }
}
