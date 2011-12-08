package test

import scala.collection.mutable.ArrayBuffer

import java.io.FileWriter

import fieldml._
import fieldml.valueType._

import fieldml.evaluator._

import framework.datastore._
import framework.value._
import framework._

import fieldml.jni.FieldmlApi._

import util.ColladaExporter
import framework.region._

object TestHierarchical
{
    def main( argv : Array[String] ) : Unit =
    {
        val regionName = "hierarchical"
        val fileName = "input\\hierarchical field test.xml"
        val meshTypeName = "mesh"
        val meshArgumentName = "mesh.argument"
        val meshCoordinatesName = "coordinates"
        val outputName = "output\\collada hierarchical.xml"

        val region = UserRegion.fromFile( regionName, fileName )

        val meshType : MeshType = region.getObject( meshTypeName )
        val meshArgument : ArgumentEvaluator = region.getObject( meshArgumentName )

        val coordinates : Evaluator = region.getObject( meshCoordinatesName )
        
        val test : Evaluator = region.getObject( meshCoordinatesName )
        
        region.bind( meshArgument, 1, 1.0, 0.99 )
        println( "*** aggregate1 = " + region.evaluate( test ) )

        val colladaXml = ColladaExporter.export2DFromFieldML( region, 32, meshArgumentName, meshCoordinatesName )
        
        val f = new FileWriter( outputName )
        f.write( colladaXml )
        f.close()
    }
}
