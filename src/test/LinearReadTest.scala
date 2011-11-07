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
import valuesource.ParameterEvaluatorValueSource

object LinearReadTest
{
    def main( argv : Array[String] ) : Unit =
    {
        val configStrings = Tuple4( "..\\Lungs\\fieldml\\P2BRP001-H653-Grown.xml", "mesh.type", "mesh.argument", "coordinates" )
        // configStrings = Tuple4( "input\\StaticAdvectionDiffusion.xml", "static_advection_diffusion.mesh", "static_advection_diffusion.mesh.argument", "static_advection_diffusion.geometric" )
        
        val region = UserRegion.fromFile( "P2BRP001-H653-Grown", configStrings._1 )

        val meshType : MeshType = region.getObject( configStrings._2 )
        val meshArgument : ArgumentEvaluator = region.getObject( configStrings._3 )

        val coordinates : Evaluator = region.getObject( configStrings._4 )
        
        val triquadNodes : ParameterEvaluator = region.getObject( "node.coordinates" )
        val nodeArgument : ArgumentEvaluator = region.getObject( "nodes.argument" )
        val rc3Argument : ArgumentEvaluator = region.getObject( "rc3.component" )
        
        region.bind( meshArgument, 20000, 0.5 )

        println( "*** aggregate = " + region.evaluate( coordinates ) )

        val colladaXml = ColladaExporter.export1DFromFieldML( region, 1, configStrings._3, configStrings._4 )

        val f = new FileWriter( "collada linear.xml" )
        f.write( colladaXml )
        f.close()
    }
}
