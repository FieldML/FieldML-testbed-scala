package fieldml.valueType

import scala.collection.BitSet

import fieldml.FieldmlObject
import fieldml.ElementSet

import fieldml.valueType.bounds._

class EnsembleType( name : String, val bounds : EnsembleBounds, val isComponent : Boolean )
    extends ValueType( name )
{
    val elementSet = new ElementSet( name + ".elements", this, ( 1 to bounds.elementCount ) )
}
