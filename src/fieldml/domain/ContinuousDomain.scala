package fieldml.domain

import fieldml._

import util.region._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.DomainBoundsType._

import util.exception._

class ContinuousDomain( name : String, val componentDomain : EnsembleDomain )
    extends Domain( name )
{
    if( ( componentDomain != null ) && ( !componentDomain.isComponent ) )
    {
        throw new FmlInvalidObjectException( componentDomain, "define ContinuousDomain components" )
    }
}


object ContinuousDomain
{
    def extract( fmlHandle : Long, objectHandle : Int, region : UserRegion ) : 
        Option[ContinuousDomain] =
    {
        var continuousDomain : ContinuousDomain = null
        
        val name = Fieldml_GetObjectName( fmlHandle, objectHandle )
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        if( objectType != FHT_CONTINUOUS_DOMAIN )
        {
            Fieldml_GetLastError( fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( "Object handle " + objectHandle + " is invalid" )
                case _ => throw new FmlTypeException( name, objectType, FHT_CONTINUOUS_DOMAIN )
            }
        }

        val componentHandle = Fieldml_GetDomainComponentEnsemble( fmlHandle, objectHandle )
        var componentDomain : EnsembleDomain = null

        if( componentHandle != FML_INVALID_HANDLE )
        {
            val componentName = Fieldml_GetObjectName( fmlHandle, componentHandle )
            
            componentDomain = region.getObject( componentName )
        }
        
        return Some( region.createContinuousDomain( name, componentDomain ) )
    }

    
    def insert( handle : Long, d : ContinuousDomain ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( d.componentDomain != null )
        {
            componentHandle = Fieldml_GetNamedObject( handle, d.componentDomain.name )
            if( componentHandle == FML_INVALID_HANDLE )
            {
                //TODO Use the right region name.
                throw new FmlUnknownObjectException( d.componentDomain.name, "" )
            }
        }
        
        val objectHandle = Fieldml_CreateContinuousDomain( handle, d.name, componentHandle )
    }
    
    
}
