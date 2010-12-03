package framework.io.serialize

import fieldml.valueType.EnsembleType
import fieldml.valueType.MeshType
import fieldml.valueType.bounds.EnsembleBounds
import fieldml.valueType.bounds.ContiguousEnsembleBounds

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.TypeBoundsType
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

object MeshTypeSerializer
{
    def insert( handle : Long, valueType : MeshType ) : Unit =
    {
        val componentHandle = GetNamedObject( handle, valueType.xiType.componentType.name )
        val objectHandle = Fieldml_CreateMeshType( handle, valueType.name, componentHandle )

        valueType.elementType.bounds match
        {
            case c : ContiguousEnsembleBounds => Fieldml_SetContiguousBoundsCount( handle, objectHandle, c.count )
            case unknown => println( "Cannot yet serialize mesh element bounds " + unknown ) 
        }
        
        valueType.shapes.default match
        {
            case s : Some[String] => Fieldml_SetMeshDefaultShape( handle, objectHandle, s.get )
            case _ =>
        }
        
        for( pair <- valueType.shapes )
        {
            Fieldml_SetMeshElementShape( handle, objectHandle, pair._1, pair._2 )
        }
        //TODO connectivity
//        for( pair <- valueType.connectivity )
//        {
//            val valueType = GetNamedObject( handle, pair._2.name )
//            val connectivity = GetNamedObject( handle, pair._1.name )
//            Fieldml_SetMeshConnectivity( handle, objectHandle, connectivity, valueType )
//        }
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : MeshType =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        val xiComponentHandle = Fieldml_GetMeshXiComponentType( source.fmlHandle, objectHandle )
        val xiComponentType = source.getEnsembleType( xiComponentHandle )
        
        val elementHandle = Fieldml_GetMeshElementType( source.fmlHandle, objectHandle )
        
        val bounds = Fieldml_GetBoundsType( source.fmlHandle, elementHandle ) match
        {
            case TypeBoundsType.BOUNDS_DISCRETE_CONTIGUOUS => new ContiguousEnsembleBounds( Fieldml_GetContiguousBoundsCount( source.fmlHandle, elementHandle ) )
            case unknown => throw new FmlException( "Cannot yet extract bounds type " + unknown )
        }
        
        return new MeshType( name, bounds, xiComponentType )
    }
}
