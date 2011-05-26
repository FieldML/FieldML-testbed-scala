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
        /*
        val regionName = "deformed_mesh"
        val fileName = "input\\DeformedMesh000.xml"
        val meshTypeName = "deformed_mesh.mesh.type"
        val meshArgumentName = "deformed_mesh.mesh.argument"
        val meshCoordinatesName = "deformed_mesh.coordinates"
        val outputName = "collada deformed mesh.xml"
          */  
        val regionName = "fullbiv_mesh"
        val fileName = "input\\UPFMean_FullBiV.xml"
        val meshTypeName = "full_biv_mesh.mesh.type"
        val meshArgumentName = "full_biv_mesh.mesh.argument"
        val meshCoordinatesName = "full_biv_mesh.coordinates"
        val outputName = "collada full_biv mesh.xml"
            
            
        val region = UserRegion.fromFile( regionName, fileName )

        val meshType : MeshType = region.getObject( meshTypeName )
        val meshArgument : ArgumentEvaluator = region.getObject( meshArgumentName )

        val coordinates : Evaluator = region.getObject( meshCoordinatesName )
        
        region.bind( meshArgument, 100, 0.5, 0.5 )

        println( "*** aggregate = " + region.evaluate( coordinates ) )

        val colladaXml = ColladaExporter.export2DTrisFromFieldML( region, 1, meshArgumentName, meshCoordinatesName )
        
        val f = new FileWriter( outputName )
        f.write( colladaXml )
        f.close()
        
        val json = JSONExporter.export2DTrisFromFieldML( region, meshArgumentName, meshCoordinatesName )
        
        val jsonFile = new FileWriter( "WebGL/test3_full_biv000.json" )
        jsonFile.write( json )
        jsonFile.close()
    }
}
