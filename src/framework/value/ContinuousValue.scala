package framework.value

import fieldml.valueType.ContinuousType

class ContinuousValue( valueType : ContinuousType, val value : Array[Double] )
    extends Value( valueType )
{
    def this( valueType : ContinuousType, values : Double* ) =
    {
        this( valueType, values.toArray )
    }
    
    
    override def cValue = value


    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[" + vType.name + ": " )
        for( v <- value )
        {
            string.append( v )
            string.append( " " )
        }
        string.append( "]" )
        
        return string.toString
    }
}
