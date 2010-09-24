package fieldml.domain

import fieldml.FieldmlObject

import fieldml.domain.bounds._

class EnsembleDomain( name : String, val bounds : EnsembleBounds, val isComponent : Boolean )
    extends Domain( name )
{
}
