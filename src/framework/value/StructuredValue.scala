package framework.value

import fieldml.valueType._

abstract class StructuredValue( vType : ValueType, tuples : Tuple2[String, Value]* )
    extends Value( vType )
{
    private val subvalues = Map[String, Value]( tuples: _* )
    
    override def subvalue( name : String ) = subvalues( name )
}
