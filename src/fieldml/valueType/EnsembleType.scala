package fieldml.valueType

import fieldml.FieldmlObject

import fieldml.valueType.bounds._

class EnsembleType( name : String, val bounds : EnsembleBounds, val isComponent : Boolean )
    extends ValueType( name )
{
}
