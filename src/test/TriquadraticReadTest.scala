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
        val library = UserRegion.library
        
        val region = UserRegion.fromFile( "heart", "D:\\Data\\Workspace\\FieldML Mockups\\FieldML 0.3 - Evaluator pipelines\\triquadratic heart test.xml" )

        val realType : ContinuousType = library.getObject( "library.real.1d" )
        val real3Type : ContinuousType = library.getObject( "library.real.3d" )
    
        val rc3ensemble : EnsembleType = library.getObject( "library.ensemble.rc.3d" )
        val real3IndexVariable : AbstractEvaluator = library.getCompanionVariable( rc3ensemble )
       
        val xi2dType : ContinuousType = library.getObject( "library.xi.2d" )
        val xi2dVar : AbstractEvaluator = library.getCompanionVariable( xi2dType )

        val meshType : MeshType = region.getObject( "test.mesh.type" )
        val meshVariable : AbstractEvaluator = region.getObject( "test.mesh" )

        val coordinates : Evaluator = region.getObject( "heart.coordinates.template" )
        
        region.bind( meshVariable, 2, 0.5, 0.5 )

        println( "*** aggregate(2, 0.5, 0.5) = " + region.evaluate( coordinates ) )
        
        val colladaXml = ColladaExporter.exportFromFieldML( region, 8, "test.mesh", "test.aggregate" )
        
        val f = new FileWriter( "collada two quads.xml" )
        f.write( colladaXml )
        f.close()
    }
}
