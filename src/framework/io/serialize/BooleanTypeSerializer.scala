package framework.io.serialize

import fieldml.valueType.EnsembleType
import fieldml.valueType.BooleanType
import util.exception._
import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._
import framework.region.UserRegion
import fieldml.valueType.BooleanType

object BooleanTypeSerializer
{
    def insert( handle : Int, valueType : BooleanType ) : Unit =
    {
        val objectHandle = Fieldml_CreateBooleanType( handle, valueType.name )
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : BooleanType =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        new BooleanType( name )
    }
}
