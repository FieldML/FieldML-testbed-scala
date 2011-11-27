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

object TestCt
{
    def main( argv : Array[String] ) : Unit =
    {
        val regionName = "btex20_comp2"
        val fileName = "input\\btex20_comp2.xml"
        val mesh1TypeName = "x_mesh"
        val mesh1ArgumentName = "x_mesh.argument"
        val mesh2TypeName = "t_mesh"
        val mesh2ArgumentName = "t_mesh.argument"
        val meshCoordinatesName = "btex20_comp2.coordinates"
        val outputName = "collada ct.xml"
        val exporter = ColladaExporter.exportFromFieldML _

        val region = UserRegion.fromFile( regionName, fileName )

        val mesh1Type : MeshType = region.getObject( mesh1TypeName )
        val mesh1Argument : ArgumentEvaluator = region.getObject( mesh1ArgumentName )

        val mesh2Type : MeshType = region.getObject( mesh2TypeName )
        val mesh2Argument : ArgumentEvaluator = region.getObject( mesh2ArgumentName )

        val coordinates : Evaluator = region.getObject( meshCoordinatesName )
        
        val test : Evaluator = region.getObject( "btex20_comp2.ct" )
        val nodes : Evaluator = region.getObject( "t_mesh.line.2_nodes" )
        val lNode : ArgumentEvaluator = region.getObject( "linear.node.argument" )
        
        region.bind( mesh1Argument, 80, 0.5 )
        region.bind( mesh2Argument, 5, 0.5 )
        println( "*** aggregate1 = " + region.evaluate( test ) )

        val colladaXml = exporter( region, 1, meshCoordinatesName, mesh1ArgumentName, mesh2ArgumentName )
        
        val f = new FileWriter( outputName )
        f.write( colladaXml )
        f.close()
    }
}
