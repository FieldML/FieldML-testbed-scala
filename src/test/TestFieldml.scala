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
import fieldml.jni.DataSourceType

import util.ColladaExporter
import framework.region._

object TestFieldml
{
    def main( argv : Array[String] ) : Unit =
    {
        val region = UserRegion.fromScratch( "test",
            "library.real.1d" -> "library.real.1d",
            "library.real.3d" -> "library.real.3d",
            "library.ensemble.rc.3d" -> "library.ensemble.rc.3d",
            "library.ensemble.rc.3d.variable" -> "library.ensemble.rc.3d.variable",
            "library.xi.2d" -> "library.xi.2d",
            "library.ensemble.xi.2d" -> "library.ensemble.xi.2d",
            "library.xi.2d.variable" -> "library.xi.2d.variable",
            "library.parameters.2d.bilinearLagrange" -> "library.parameters.2d.bilinearLagrange",
            "library.parameters.2d.bilinearLagrange.variable" -> "library.parameters.2d.bilinearLagrange.variable",
            "library.localNodes.2d.square2x2" -> "library.localNodes.2d.square2x2",
            "library.localNodes.2d.square2x2.variable" -> "library.localNodes.2d.square2x2.variable",
            "library.interpolator.2d.unit.bilinearLagrange" -> "library.interpolator.2d.unit.bilinearLagrange"
            )

        val realType : ContinuousType = region.getObject( "library.real.1d" )
        val real3Type : ContinuousType = region.getObject( "library.real.3d" )
    
        val rc3ensemble : EnsembleType = region.getObject( "library.ensemble.rc.3d" )
        val real3IndexVariable : ArgumentEvaluator = region.getObject( "library.ensemble.rc.3d.variable" )
       
        val xi2dVar : ArgumentEvaluator = region.getObject( "library.xi.2d.variable" )

        val meshType = region.createMeshType( "test.mesh.type", 2, 2 )
        val meshVariable = region.createArgumentEvaluator( "test.mesh", meshType )
        val elementVariable = region.createSubtypeEvaluator( meshVariable, "element" )
        val xiVariable = region.createSubtypeEvaluator( meshVariable, "xi" )

        val nodes = region.createEnsembleType( "test.nodes.type", 6, false )
        val nodesVariable = region.createArgumentEvaluator( "test.nodes", nodes )
        
        val bilinearParametersType : ContinuousType = region.getObject( "library.parameters.2d.bilinearLagrange" )
        val bilinearParametersVariable : ArgumentEvaluator = region.getObject( "library.parameters.2d.bilinearLagrange.variable" )
        val bilinearIndexVariable : ArgumentEvaluator = region.getObject( "library.localNodes.2d.square2x2.variable" )
        
        val firstInterpolator = region.createReferenceEvaluator( "test.interpolator_v1", "library.interpolator.2d.unit.bilinearLagrange", region )
        firstInterpolator.bind( xi2dVar -> xiVariable )
        
        val secondInterpolator = region.createReferenceEvaluator( "test.interpolator_v2", "library.interpolator.2d.unit.bilinearLagrange", region )
        secondInterpolator.bind( xi2dVar -> xiVariable )
        
        val parameterDescription = new SemidenseDataDescription( realType, Array( real3IndexVariable, nodesVariable ), Array() )
        val parameterSource = region.createTextFileResource( "test.parameters.resource", "test_fieldml_nodal_params" )
        val parameterData = region.createTextDataSource( "test.parameters.data", parameterSource, 1, 6, 3, 0, 0 )
        val parameters = region.createParameterEvaluator( "test.parameters", realType, parameterData, parameterDescription )
        
        parameters( 1 ) = ( 0.0, 0.0, 1.0 )
        parameters( 2 ) = ( 0.0, 1.0, 1.5 )
        parameters( 3 ) = ( 0.0, 2.0, 2.0 )
        parameters( 4 ) = ( 1.0, 0.0, 2.5 )
        parameters( 5 ) = ( 1.0, 1.0, 3.0 )
        parameters( 6 ) = ( 1.0, 2.0, 3.5 )
        
        println( "Parameters( 6, 2 ) = " + parameters( 6, 2 ) )
        println( "Parameters( 2 ) = " + parameters( 2 ) )
        println( "Parameters( 1 ) = " + parameters( 1 ) )
        
        val connectivityDescription = new SemidenseDataDescription( nodes, Array( bilinearIndexVariable, elementVariable ), Array() )
        val connectivitySource = region.createTextFileResource( "test.connectivity.resource", "test_fieldml_connectivity" )
        val connectivityData = region.createTextDataSource( "test.connectivity.data", connectivitySource, 1, 2, 4, 0, 0 )
        val connectivity = region.createParameterEvaluator( "test.connectivity", nodes, connectivityData, connectivityDescription )
        
        connectivity( 1 ) = ( 1, 4, 2, 5 )
        connectivity( 2 ) = ( 2, 5, 3, 6 )

        val piecewise = region.createPiecewiseEvaluator( "test.piecewise", elementVariable, realType )
        piecewise.map( 1 -> firstInterpolator )
        piecewise.map( 2 -> secondInterpolator )
        
        println( "*** piecewise(?) = " + region.evaluate( piecewise ) )
        
        val bilinearParameters = region.createAggregateEvaluator( "test.bilinear_parameters", bilinearParametersType ) 
        bilinearParameters.bind_index( 1 -> bilinearIndexVariable )
        bilinearParameters.bind( nodesVariable -> connectivity )
        bilinearParameters.setDefault( parameters )

        piecewise.bind( bilinearParametersVariable -> bilinearParameters )
        
        region.bind( meshVariable, 2, 0, 0 )
        region.bind( real3IndexVariable, 3 )

        println( "*** piecewise(2, 0, 0) = " + region.evaluate( piecewise ) )
        
        region.bind( meshVariable, 2, 1, 0 )

        println( "*** piecewise(2, 1, 0) = " + region.evaluate( piecewise ) )

        region.bind( meshVariable, 2, 0, 1 )

        println( "*** piecewise(2, 0, 1) = " + region.evaluate( piecewise ) )
        
        region.bind( meshVariable, 2, 1, 1 )

        println( "*** piecewise(2, 1, 1) = " + region.evaluate( piecewise ) )

        val aggregate = region.createAggregateEvaluator( "test.aggregate", real3Type )
        aggregate.bind_index( 1 -> real3IndexVariable )
        aggregate.map( 1 -> piecewise )
        aggregate.map( 2 -> piecewise )
        aggregate.map( 3 -> piecewise )
        
        region.bind( meshVariable, 2, 0.5, 0.5 )

        println( "*** aggregate(2, 0.5, 0.5) = " + region.evaluate( aggregate ) )
        
        val colladaXml = ColladaExporter.export2DFromFieldML( region, 8, "test.mesh", "test.aggregate" )
        
        val f = new FileWriter( "collada two quads.xml" )
        f.write( colladaXml )
        f.close()

        region.serialize()
    }
}
