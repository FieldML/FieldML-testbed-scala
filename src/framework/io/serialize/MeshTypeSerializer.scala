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
        
        val xiHandle = Fieldml_CreateMeshChartType( handle, objectHandle, valueType.xiType.name )
        Fieldml_CreateContinuousTypeComponents( handle, xiHandle, valueType.xiType.componentType.name, valueType.xiType.componentType.elementCount )
        
        val elementsHandle = Fieldml_CreateMeshElementsType( handle, objectHandle, valueType.elementType.name )

        EnsembleTypeSerializer.insertElements( handle, elementsHandle, valueType.elementType )
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : MeshType =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        val xiComponentHandle = Fieldml_GetMeshChartComponentType( source.fmlHandle, objectHandle )
        val xiComponentType = source.getEnsembleType( xiComponentHandle )
        
        val elementHandle = Fieldml_GetMeshElementsType( source.fmlHandle, objectHandle )
        
        val meshName = Fieldml_GetObjectDeclaredName( source.fmlHandle, objectHandle )
        val elementName = Fieldml_GetObjectDeclaredName( source.fmlHandle, Fieldml_GetMeshElementsType( source.fmlHandle, objectHandle ) )
        val xiName = Fieldml_GetObjectDeclaredName( source.fmlHandle, Fieldml_GetMeshChartType( source.fmlHandle, objectHandle ) )
        
        if( !elementName.startsWith( meshName ) || !xiName.startsWith( meshName ) )
        {
            throw new FmlInvalidObjectException( "Mesh " + meshName + " has incorrectly named components" )
        }
                
        val mesh = new MeshType( name, xiComponentType, elementName.stripPrefix( meshName + "." ), xiName.stripPrefix( meshName + "." ) )
        
        EnsembleTypeSerializer.extractElements( source, objectHandle, mesh.elementType )
        
        mesh.shapes = source.getEvaluator( Fieldml_GetMeshShapes( source.fmlHandle, objectHandle ) )
        
        mesh
    }
}
