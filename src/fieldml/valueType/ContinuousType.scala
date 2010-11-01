package fieldml.valueType

import fieldml._

import util.exception._

class ContinuousType( name : String, val componentType : EnsembleType )
    extends ValueType( name )
{
    if( ( componentType != null ) && ( !componentType.isComponent ) )
    {
        throw new FmlInvalidObjectException( componentType, "define ContinuousDomain components" )
    }
}
