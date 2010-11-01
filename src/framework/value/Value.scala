package framework.value

import fieldml.valueType._

import framework.FmlException

abstract class Value( val vType : ValueType )
{
    def cValue : Array[Double] = throw new FmlException( "Value type " + vType + " is not real-valued" )
    
    def eValue : Int = throw new FmlException( "Value type " + vType + " is not ensemble-valued" )
    
    def subvalue( name : String ) : Value = throw new FmlException( "Value type " + vType + " is not structured" )
}


object Value
{
    def apply( vType : ValueType, value : Int ) : Value =
    {
        vType match
        {
            case t : EnsembleType => new EnsembleValue( t, value )
            case t : ContinuousType => new ContinuousValue( t, value )
            case _ => throw new FmlException( "Cannot create a " + vType.name + " value with " + value )
        }
    }


    def apply( vType : ValueType, values : Double* ) : Value =
    {
        vType match
        {
            case t : ContinuousType => new ContinuousValue( t, values: _* )
            case _ => throw new FmlException( "Cannot create a " + vType.name + " value with " + values )
        }
    }
}