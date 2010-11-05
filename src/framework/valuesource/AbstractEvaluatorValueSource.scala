package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType.ValueType
import fieldml.FieldmlObject
import fieldml.evaluator.Evaluator
import fieldml.evaluator.AbstractEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class AbstractEvaluatorValueSource( name : String, valueType : ValueType, explicitVariables : AbstractEvaluator* )
    extends AbstractEvaluator( name, valueType, explicitVariables:_* )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        state.pushAndApply( binds.toSeq )
        
        val value = state.getBind( this ) match
        {
            case s : Some[Evaluator] => s.get.evaluate( state )
            case None => None
        }
        
        state.pop()
        
        return value
    }
}
