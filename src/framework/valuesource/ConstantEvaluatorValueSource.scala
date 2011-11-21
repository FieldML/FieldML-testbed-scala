package framework.valuesource

import fieldml.valueType._
import fieldml.evaluator.ConstantEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ConstantEvaluatorValueSource( name : String, valueString : String, valueType : ValueType )
    extends ConstantEvaluator( name, valueString, valueType )
    with ValueSource
{
    val value : Value = Value( valueType, valueString )
    
    override def variables = None

    override def toString() : String =
    {
        return "(" + value + ")[ConstantEvaluatorValueSource]"
    }
    
    
    private val _value = Some( value )
    
    override def evaluate( state : EvaluationState ) : Option[Value] = _value
}
