package framework.valuesource

import fieldml.valueType._

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ConstantValueSource( valueType : ValueType, _value : Value )
    extends ValueSource( valueType )
{
    override def toString() : String =
    {
        return "(" + valueType + " = " + _value + ")[ConstantValueSource]"
    }
    
    
    private val value = Some( _value )
    
    override def getValue( state : EvaluationState ) : Option[Value] = value
}
