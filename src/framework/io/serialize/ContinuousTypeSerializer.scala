package framework.io.serialize

import framework.io.serialize._
import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._

import framework.region.UserRegion


object ContinuousTypeSerializer
{
    def insert( handle : Long, valueType : ContinuousType ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( valueType.componentType != null )
        {
            componentHandle = GetNamedObject( handle, valueType.componentType.name )
        }
        
        val objectHandle = Fieldml_CreateContinuousType( handle, valueType.name, componentHandle )
    }


    def extract( source : Deserializer, objectHandle : Int ) : ContinuousType =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val componentHandle = Fieldml_GetTypeComponentEnsemble( source.fmlHandle, objectHandle )
        
        val componentType = componentHandle match
        {
            case FML_INVALID_HANDLE => null
            case _ => source.getEnsembleType( componentHandle )
        }
        
        return new ContinuousType( name, componentType )
    }
}