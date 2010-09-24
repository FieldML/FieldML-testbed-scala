package framework.io.serialize

import fieldml.domain.MeshDomain

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._

import fieldml.domain.bounds.ContiguousEnsembleBounds

class MeshDomainSerializer( val domain : MeshDomain )
{
    def insert( handle : Long ) : Unit =
    {
        val componentHandle = GetNamedObject( handle, domain.xiDomain.componentDomain.name )
        val objectHandle = Fieldml_CreateMeshDomain( handle, domain.name, componentHandle )

        domain.elementBounds match
        {
            case c : ContiguousEnsembleBounds => Fieldml_SetContiguousBoundsCount( handle, objectHandle, c.count )
            case unknown => println( "Cannot yet serialize mesh element bounds " + unknown ) 
        }
        
        domain.shapes.default match
        {
            case s : Some[String] => Fieldml_SetMeshDefaultShape( handle, objectHandle, s.get )
            case _ =>
        }
        
        for( pair <- domain.shapes )
        {
            Fieldml_SetMeshElementShape( handle, objectHandle, pair._1, pair._2 )
        }
        
        for( pair <- domain.connectivity )
        {
            val domain = GetNamedObject( handle, pair._2.name )
            val connectivity = GetNamedObject( handle, pair._1.name )
            Fieldml_SetMeshConnectivity( handle, objectHandle, connectivity, domain )
        }
    }

}
