package framework.valuesource

import fieldml.valueType._
import fieldml.evaluator.Evaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ConstantValueSource( val value : Value )
    extends Evaluator( value.toString, value.vType )
    with ValueSource
{
    override def variables = None
    
    
    override def toString() : String =
    {
        return "(" + value + ")[ConstantValueSource]"
    }
    
    
    private val _value = Some( value )
    
    override def evaluate( state : EvaluationState ) : Option[Value] = _value
}
