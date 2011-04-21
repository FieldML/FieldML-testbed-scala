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
    def insert( handle : Int, valueType : ContinuousType ) : Unit =
    {
        val objectHandle = Fieldml_CreateContinuousType( handle, valueType.name )
        
        if( valueType.componentType != null )
        {
            Fieldml_CreateContinuousTypeComponents( handle, objectHandle, valueType.componentType.name, valueType.componentType.elementCount )
        }
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