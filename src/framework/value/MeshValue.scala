package framework.value

import fieldml.domain.ContinuousDomain

class MeshValue( val elementValue : Int, val xiValue : Array[Double] )
    extends Value()
{
    def this( elementValue : Int, xiValue : Double* ) =
    {
        this( elementValue, xiValue.toArray )
    }


    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[ " )
        string.append( elementValue )
        string.append( " " )
        for( v <- xiValue )
        {
            string.append( v )
            string.append( " " )
        }
        string.append( "]" )
        
        return string.toString
    }
}
