package fieldml.domain

import fieldml._

import util.exception._

class ContinuousDomain( name : String, val componentDomain : EnsembleDomain )
    extends Domain( name )
{
    if( ( componentDomain != null ) && ( !componentDomain.isComponent ) )
    {
        throw new FmlInvalidObjectException( componentDomain, "define ContinuousDomain components" )
    }
}
