package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType.ValueType
import fieldml.FieldmlObject
import fieldml.evaluator.SubtypeEvaluator

import framework.value.Value
import framework.value.EnsembleValue
import framework.value.ContinuousValue
import framework.value.StructuredValue
import framework.Context
import framework.EvaluationState

class SubtypeValueSource( evaluator : SubtypeEvaluator )
    extends ValueSource( evaluator.valueType )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        state.get( evaluator ) match
        {
            case m : Some[StructuredValue] => Some( m.get.subvalue( evaluator.subname ) ) 
            case _ => return None
        }
    }
}
