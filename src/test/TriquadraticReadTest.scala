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

object TriquadraticReadTest
{
    def main( argv : Array[String] ) : Unit =
    {
        val source = "input\\triquadratic heart test.xml"
        val meshTypeName = "heart.mesh.type"
        val meshArgumentName = "heart.mesh.argument"
        val outputValues = "heart.coordinates"
          
        val region = UserRegion.fromFile( "heart", source )

        val meshType : MeshType = region.getObject( meshTypeName )
        val meshArgument : ArgumentEvaluator = region.getObject( meshArgumentName )

        val coordinates : Evaluator = region.getObject( outputValues )
        
        region.bind( meshArgument, 1, 0.5, 0.5, 0.5 )

        println( "*** aggregate = " + region.evaluate( coordinates ) )

        val colladaXml = ColladaExporter.export3DFromFieldML( region, 8, meshArgumentName, outputValues )
        
        val f = new FileWriter( "collada heart.xml" )
        f.write( colladaXml )
        f.close()
    }
}
