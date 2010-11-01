package framework.io.serialize

import framework.io.serialize._
import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._

import framework.region.UserRegion


class ContinuousTypeSerializer( val valueType : ContinuousType )
{
    def insert( handle : Long ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( valueType.componentType != null )
        {
            componentHandle = GetNamedObject( handle, valueType.componentType.name )
        }
        
        val objectHandle = Fieldml_CreateContinuousDomain( handle, valueType.name, componentHandle )
    }
}

object ContinuousTypeSerializer
{
    def extract( fmlHandle : Long, objectHandle : Int, region : UserRegion ) : 
        Option[ContinuousType] =
    {
        var continuousType : ContinuousType = null
        
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
        var componentType : EnsembleType = null

        if( componentHandle != FML_INVALID_HANDLE )
        {
            val componentName = Fieldml_GetObjectName( fmlHandle, componentHandle )
            
            componentType = region.getObject( componentName )
        }
        
        return Some( region.createContinuousType( name, componentType ) )
    }
}