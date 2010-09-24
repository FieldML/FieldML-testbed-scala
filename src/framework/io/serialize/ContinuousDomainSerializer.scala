package framework.io.serialize

import framework.io.serialize._
import fieldml.domain.ContinuousDomain
import fieldml.domain.EnsembleDomain

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._

import framework.region.UserRegion

class ContinuousDomainSerializer( val domain : ContinuousDomain )
{
    def insert( handle : Long ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( domain.componentDomain != null )
        {
            componentHandle = GetNamedObject( handle, domain.componentDomain.name )
        }
        
        val objectHandle = Fieldml_CreateContinuousDomain( handle, domain.name, componentHandle )
    }
}


object ContinuousDomainSerializer
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
}