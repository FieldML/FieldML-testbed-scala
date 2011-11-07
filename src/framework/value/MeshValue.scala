package framework.value

import fieldml.valueType.MeshType

class MeshValue( vType : MeshType, val elementValue : Int, val xiValue : Double* )
    extends StructuredValue( vType,
        Tuple2( vType.elementName, new EnsembleValue( vType.elementType, elementValue ) ),
        Tuple2( vType.xiName, new ContinuousValue( vType.xiType, xiValue:_* ) )
        )
{
    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[" + vType.name + ": " )
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
