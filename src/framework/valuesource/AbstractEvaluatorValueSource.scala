package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType.ValueType
import fieldml.FieldmlObject
import fieldml.evaluator.AbstractEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class AbstractEvaluatorValueSource( evaluator : AbstractEvaluator )
    extends EvaluatorValueSource( evaluator )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        return state.get( state.getBind( evaluator ) )
    }
}
