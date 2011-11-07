package framework.value

import fieldml.valueType.BooleanType

import framework.FmlException

class BooleanValue( valueType : BooleanType, val value : Boolean )
    extends Value( valueType )
{
    override def bValue = value

    
    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[" + vType.name + ": " )
        string.append( value )
        string.append( "]" )
        
        return string.toString
    }
}


object BooleanValue
{
    def apply( vType : BooleanType, value : Boolean ) : Value =
    {
        new BooleanValue( vType, value )
    }
}
