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
import util.JSONExporter
import framework.region._

object BilinearSimplexReadTest
{
    def main( argv : Array[String] ) : Unit =
    {
        val region = UserRegion.fromFile( "deformed_mesh", "input\\DeformedMesh000.xml" )

        val meshType : MeshType = region.getObject( "deformed_mesh.mesh.type" )
        val meshVariable : AbstractEvaluator = region.getObject( "deformed_mesh.mesh.variable" )

        val coordinates : Evaluator = region.getObject( "deformed_mesh.coordinates" )
        
        region.bind( meshVariable, 100, 0.5, 0.5 )

        println( "*** aggregate = " + region.evaluate( coordinates ) )

        val colladaXml = ColladaExporter.export2DTrisFromFieldML( region, 1, "deformed_mesh.mesh.variable", "deformed_mesh.coordinates" )
        
        val f = new FileWriter( "collada deformed mesh.xml" )
        f.write( colladaXml )
        f.close()
        
        val json = JSONExporter.export2DTrisFromFieldML( region, "deformed_mesh.mesh.variable", "deformed_mesh.coordinates" )
        
        val jsonFile = new FileWriter( "WebGL/test3_deformed000.json" )
        jsonFile.write( json )
        jsonFile.close()
    }
}
