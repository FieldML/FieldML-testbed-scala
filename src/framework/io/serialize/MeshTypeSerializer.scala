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
        val objectHandle = Fieldml_CreateMeshType( handle, valueType.name, componentHandle )

        
        val elementArray = valueType.elementType.elementSet.toArray
        
        Fieldml_AddEnsembleElements( handle, objectHandle, elementArray, elementArray.size ) 
        
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
        
        val elementHandle = Fieldml_GetMeshElementType( source.fmlHandle, objectHandle )
                
        val mesh = new MeshType( name, xiComponentType )

        val count = Fieldml_GetElementCount( source.fmlHandle, objectHandle )
        
        val values = new Array[Int]( count )
        Fieldml_GetElementEntries( source.fmlHandle, objectHandle, 1, values, count )
        
        for( i <- values )
        {
            mesh.elementType.elementSet.add( i )
        }
        
        mesh
    }
}
