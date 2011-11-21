package framework.value

import fieldml.valueType._

import framework.FmlException

abstract class Value( val vType : ValueType )
{
    def cValue : Array[Double] = throw new FmlException( "Value type " + vType + " is not real-valued" )
    
    def eValue : Int = throw new FmlException( "Value type " + vType + " is not ensemble-valued" )
    
    def bValue : Boolean = throw new FmlException( "Value type " + vType + " is not boolean-valued" )
    
    def subvalue( name : String ) : Value = throw new FmlException( "Value type " + vType + " is not structured" )
}


object Value
{
    def apply( vType : ValueType, values : Double* ) : Value =
    {
        vType match
        {
            case t : EnsembleType => EnsembleValue( t, values: _* )
            case t : ContinuousType => ContinuousValue( t, values: _* )
            case _ => throw new FmlException( "Cannot create a " + vType.name + " value with " + values )
        }
    }

    def apply( vType : ValueType, value : Boolean ) : Value =
    {
        vType match
        {
            case t : BooleanType => BooleanValue( t, value )
            case _ => throw new FmlException( "Cannot create a " + vType.name + " value with " + value )
        }
    }
    
    
    def apply( vType : ValueType, value : String ) : Value =
    {
        //TODO Currently only supports scalar values
        vType match
        {
            case t : BooleanType => BooleanValue( t, value.toBoolean )
            case t : ContinuousType => ContinuousValue( t, value.toDouble )
            case t : EnsembleType => EnsembleValue( t, value.toInt )
        }
    }
}