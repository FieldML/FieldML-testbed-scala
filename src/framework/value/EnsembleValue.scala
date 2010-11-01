package framework.value

import fieldml.valueType.EnsembleType

class EnsembleValue( valueType : EnsembleType, val value : Int )
    extends Value( valueType )
{
    override def eValue = value

    
    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[ " )
        string.append( value )
        string.append( "]" )
        
        return string.toString
    }
}
