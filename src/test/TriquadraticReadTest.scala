package test

import scala.collection.mutable.ArrayBuffer

import java.io.FileWriter

import fieldml._
import fieldml.valueType._
import fieldml.valueType.bounds._

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
        val region = UserRegion.fromFile( "heart", "input\\triquadratic heart test.xml" )

        val meshType : MeshType = region.getObject( "heart.mesh.type" )
        val meshVariable : AbstractEvaluator = region.getObject( "heart.mesh.variable" )

        val coordinates : Evaluator = region.getObject( "heart.coordinates" )
        
        region.bind( meshVariable, 1, 0.5, 0.5, 0.5 )

        println( "*** aggregate = " + region.evaluate( coordinates ) )
        
        val colladaXml = ColladaExporter.export3DFromFieldML( region, 16, "heart.mesh.variable", "heart.coordinates" )
        
        val f = new FileWriter( "collada line.xml" )
        f.write( colladaXml )
        f.close()
    }
}
