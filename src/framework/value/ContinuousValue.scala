package framework.value

import fieldml.domain.ContinuousDomain

class ContinuousValue( val value : Array[Double] )
    extends Value()
{
    def this( values : Double* ) =
    {
        this( values.toArray )
    }


    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[ " )
        for( v <- value )
        {
            string.append( v )
            string.append( " " )
        }
        string.append( "]" )
        
        return string.toString
    }
}
