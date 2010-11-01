package framework.io.serialize

import fieldml.valueType.MeshType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._

import fieldml.valueType.bounds.ContiguousEnsembleBounds

class MeshTypeSerializer( val valueType : MeshType )
{
    def insert( handle : Long ) : Unit =
    {
        val componentHandle = GetNamedObject( handle, valueType.xiType.componentType.name )
        val objectHandle = Fieldml_CreateMeshDomain( handle, valueType.name, componentHandle )

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

}
