package framework.value

import fieldml.domain.EnsembleDomain

class EnsembleValue( val value : Int )
    extends Value()
{
    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[ " )
        string.append( value )
        string.append( "]" )
        
        return string.toString
    }
}
