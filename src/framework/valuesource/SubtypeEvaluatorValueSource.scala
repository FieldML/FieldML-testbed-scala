package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType.ValueType
import fieldml.evaluator.Evaluator
import fieldml.evaluator.SubtypeEvaluator

import framework.value.Value
import framework.value.EnsembleValue
import framework.value.ContinuousValue
import framework.value.StructuredValue
import framework.Context
import framework.EvaluationState

class SubtypeEvaluatorValueSource( baseEvaluator : Evaluator, subname : String )
    extends SubtypeEvaluator( baseEvaluator, subname )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        baseEvaluator.evaluate( state ) match
        {
            case m : Some[StructuredValue] => Some( m.get.subvalue( subname ) ) 
            case _ => return None
        }
    }
}
