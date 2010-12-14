package fieldml.valueType

import scala.collection.immutable.Map

import framework.FmlException

class StructuredType( name : String, _subtypes : Tuple2[String, ValueType]* )
    extends ValueType( name )
{
    private val subtypes = Map[String, ValueType]( _subtypes: _* )
    
    def subtype( subname : String ) = subtypes( subname )
    
    def subNames = subtypes.keys
}
