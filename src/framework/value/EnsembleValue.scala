package framework.value

import fieldml.valueType.EnsembleType

import framework.FmlException

class EnsembleValue( valueType : EnsembleType, val value : Int )
    extends Value( valueType )
{
    override def eValue = value

    
    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[" + vType.name + ": " )
        string.append( value )
        string.append( "]" )
        
        return string.toString
    }
}


object EnsembleValue
{
    def apply( vType : EnsembleType, values : Double* ) : Value =
    {
        if( values.length > 1 )
        {
            throw new FmlException( "Multi-component ensemble values not supported." );
        }

        new EnsembleValue( vType, values(0).toInt )
    }
}
