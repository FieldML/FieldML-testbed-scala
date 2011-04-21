package framework.io.serialize

import fieldml.valueType.EnsembleType
import fieldml.valueType.MeshType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

object MeshTypeSerializer
{
    def insert( handle : Int, valueType : MeshType ) : Unit =
    {
        val componentHandle = GetNamedObject( handle, valueType.xiType.componentType.name )
        val objectHandle = Fieldml_CreateMeshType( handle, valueType.name )
        
        val xiHandle = Fieldml_CreateMeshXiType( handle, objectHandle, valueType.xiType.name )
        Fieldml_CreateContinuousTypeComponents( handle, xiHandle, valueType.xiType.componentType.name, valueType.xiType.componentType.elementCount )
        
        val elementsHandle = Fieldml_CreateMeshElementsType( handle, objectHandle, valueType.elementType.name )

        EnsembleTypeSerializer.insertElements( handle, elementsHandle, valueType.elementType )
        
        valueType.shapes.default match
        {
            case s : Some[String] => Fieldml_SetMeshDefaultShape( handle, objectHandle, s.get )
            case _ =>
        }
        
        for( pair <- valueType.shapes )
        {
            Fieldml_SetMeshElementShape( handle, objectHandle, pair._1, pair._2 )
        }
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : MeshType =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        val xiComponentHandle = Fieldml_GetMeshXiComponentType( source.fmlHandle, objectHandle )
        val xiComponentType = source.getEnsembleType( xiComponentHandle )
        
        val elementHandle = Fieldml_GetMeshElementsType( source.fmlHandle, objectHandle )
                
        val mesh = new MeshType( name, xiComponentType )
        
        EnsembleTypeSerializer.extractElements( source, objectHandle, mesh.elementType )
        
        mesh
    }
}
