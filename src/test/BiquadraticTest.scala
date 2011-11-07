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

object BiquadraticTest
{
    def main( argv : Array[String] ) : Unit =
    {
        val region = UserRegion.fromScratch( "test",
            "library.real.1d" -> "real.1d",
            "library.real.3d" -> "real.3d",
            "library.ensemble.rc.3d" -> "coordinates.rc.3d.component",
            "library.ensemble.rc.3d.argument" -> "coordinates.rc.3d.component.argument",
            "library.xi.2d" -> "chart.2d",
            "library.xi.2d.argument" -> "chart.2d.argument",

            "library.parameters.2d.bilinearLagrange" -> "parameters.2d.bilinearLagrange",
            "library.parameters.2d.bilinearLagrange.argument" -> "parameters.2d.bilinearLagrange.argument",
            "library.localNodes.2d.square2x2.argument" -> "localNodes.2d.square2x2.argument",
            "library.interpolator.2d.unit.bilinearLagrange" -> "interpolator.2d.unit.bilinearLagrange",

            "library.parameters.2d.biquadraticLagrange" -> "parameters.2d.biquadraticLagrange",
            "library.parameters.2d.biquadraticLagrange.argument" -> "parameters.2d.biquadraticLagrange.argument",
            "library.localNodes.2d.square3x3.argument" -> "localNodes.2d.square3x3.argument",
            "library.interpolator.2d.unit.biquadraticLagrange" -> "interpolator.2d.unit.biquadraticLagrange"
        )



        val realType : ContinuousType = region.getObject( "library.real.1d" )
        val real3Type : ContinuousType = region.getObject( "library.real.3d" )
    
        val rc3ensemble : EnsembleType = region.getObject( "library.ensemble.rc.3d" )
        val real3IndexVariable : ArgumentEvaluator = region.getObject( "library.ensemble.rc.3d.argument" )
       
        val xi2dType : ContinuousType = region.getObject( "library.xi.2d" )
        val xi2dVar : ArgumentEvaluator = region.getObject( "library.xi.2d.argument" )

        val meshType = region.createMeshType( "test.mesh.type", 9, 2 )
        val meshVariable = region.createArgumentEvaluator( "test.mesh", meshType )
        val elementVariable = region.createSubtypeEvaluator( meshVariable, "element" )
        val xiVariable = region.createSubtypeEvaluator( meshVariable, "xi" )

        val nodes = region.createEnsembleType( "test.nodes.type", 48, false )
        val nodesVariable = region.createArgumentEvaluator( "test.nodes", nodes )
        
        val bilinearParametersType : ContinuousType = region.getObject( "library.parameters.2d.bilinearLagrange" )
        val bilinearParametersVariable : ArgumentEvaluator = region.getObject( "library.parameters.2d.bilinearLagrange.argument" )
        val bilinearIndexVariable : ArgumentEvaluator = region.getObject( "library.localNodes.2d.square2x2.argument" )
        
        val biquadraticParametersType : ContinuousType = region.getObject( "library.parameters.2d.biquadraticLagrange" )
        val biquadraticParametersVariable : ArgumentEvaluator = region.getObject( "library.parameters.2d.biquadraticLagrange.argument" )
        val biquadraticIndexVariable : ArgumentEvaluator = region.getObject( "library.localNodes.2d.square3x3.argument" )
        
        val bilinearInterpolator = region.createReferenceEvaluator( "test.bilinear_interpolator", "library.interpolator.2d.unit.bilinearLagrange", region )
        bilinearInterpolator.bind( xi2dVar -> xiVariable )
        
        val biquadraticInterpolator = region.createReferenceEvaluator( "test.biquadratic_interpolator", "library.interpolator.2d.unit.biquadraticLagrange", region )
        biquadraticInterpolator.bind( xi2dVar -> xiVariable )
        
        val parameterDescription = new DenseDataDescription( realType, Array( real3IndexVariable, nodesVariable ) )
        val parameterResource = region.createTextInlineResource( "test.parameters.resource", "" )
        val parameterData = region.createArrayDataSource( "test.parameters.data", parameterResource, "1", 2 )
        parameterData.rawSizes = Array( 48, 3 )
        parameterData.sizes = Array( 48, 3 )
        
        val parameters = region.createParameterEvaluator( "test.parameters", realType, parameterData, parameterDescription )
        
        parameters( 1 ) = ( 0.0, 0.0, 1.0 ); parameters( 2 ) = ( 1.0, 0.0, 1.0 ); parameters( 3 ) = ( 2.0, 0.0, 1.0 )
        parameters( 4 ) = ( 3.0, 0.0, 1.0 ); parameters( 5 ) = ( 0.0, 1.0, 1.0 ); parameters( 6 ) = ( 1.0, 1.0, 0.0 )
        parameters( 7 ) = ( 2.0, 1.0, 0.0 ); parameters( 8 ) = ( 3.0, 1.0, 1.0 ); parameters( 9 ) = ( 0.0, 2.0, 1.0 )
        parameters( 10 ) = ( 1.0, 2.0, 0.0 ); parameters( 11 ) = ( 2.0, 2.0, 0.0 ); parameters( 12 ) = ( 3.0, 2.0, 1.0 )
        parameters( 13 ) = ( 0.0, 3.0, 1.0 ); parameters( 14 ) = ( 1.0, 3.0, 1.0 ); parameters( 15 ) = ( 2.0, 3.0, 1.0 )
        parameters( 16 ) = ( 3.0, 3.0, 1.0 ); parameters( 17 ) = ( 0.5, 0.0, 1.0 ); parameters( 18 ) = ( 1.5, 0.0, 1.0 )
        parameters( 19 ) = ( 2.5, 0.0, 1.0 ); parameters( 20 ) = ( 0.0, 0.5, 1.0 ); parameters( 21 ) = ( 0.5, 0.5, 0.5 )
        parameters( 22 ) = ( 1.0, 0.5, 0.25 ); parameters( 23 ) = ( 1.5, 0.5, 0.25 ); parameters( 24 ) = ( 2.0, 0.5, 0.25 )
        parameters( 25 ) = ( 2.5, 0.5, 0.5 ); parameters( 26 ) = ( 3.0, 0.5, 1.0 ); parameters( 27 ) = ( 0.5, 1.0, 0.25 )
        parameters( 28 ) = ( 1.5, 1.0, 0.0 ); parameters( 29 ) = ( 2.5, 1.0, 0.25 ); parameters( 30 ) = ( 0.0, 1.5, 1.0 )
        parameters( 31 ) = ( 0.5, 1.5, 0.25 ); parameters( 32 ) = ( 1.0, 1.5, 0.0 ); parameters( 33 ) = ( 2.0, 1.5, 0.0 )
        parameters( 34 ) = ( 2.5, 1.5, 0.25 ); parameters( 35 ) = ( 3.0, 1.5, 1.0 ); parameters( 36 ) = ( 0.5, 2.0, 0.25 )
        parameters( 37 ) = ( 1.5, 2.0, 0.0 ); parameters( 38 ) = ( 2.5, 2.0, 0.25 ); parameters( 39 ) = ( 0.0, 2.5, 1.0 )
        parameters( 40 ) = ( 0.5, 2.5, 0.5 ); parameters( 41 ) = ( 1.0, 2.5, 0.25 ); parameters( 42 ) = ( 1.5, 2.5, 0.25 )
        parameters( 43 ) = ( 2.0, 2.5, 0.25 ); parameters( 44 ) = ( 2.5, 2.5, 0.5 ); parameters( 45 ) = ( 3.0, 2.5, 1.0 )
        parameters( 46 ) = ( 0.5, 3.0, 1.0 ); parameters( 47 ) = ( 1.5, 3.0, 1.0 ); parameters( 48 ) = ( 2.5, 3.0, 1.0 )
        
        val bilinearElementSet = Array[Int]( 5 )
        val bilinearParameterSet = null
        
        val bilinearConnectivityDesc = new DokDataDescription( nodes, Array( bilinearParameterSet, bilinearElementSet  ), Array( bilinearIndexVariable, elementVariable ), Array() )
        val bilinearConnectivityResource = region.createTextInlineResource( "test.bilinear_connectivity.resource", "" )
        val bilinearConnectivityData = region.createArrayDataSource( "test.bilinear_connectivity.data", bilinearConnectivityResource, "1", 2 )
        bilinearConnectivityData.rawSizes = Array( 1, 4 )
        bilinearConnectivityData.sizes = Array( 1, 4 )
        val bilinearConnectivity = region.createParameterEvaluator( "test.bilinear_connectivity", nodes, bilinearConnectivityData, bilinearConnectivityDesc )
        
        bilinearConnectivity( 5 ) = ( 6, 7, 10, 11 ) 

        val biquadraticElementSet = Array[Int]( 1, 2, 3, 4, 6, 7, 8, 9 )
        val biquadraticParameterSet = null
        
        val biquadraticConnectivityDesc = new DokDataDescription( nodes, Array( biquadraticParameterSet, biquadraticElementSet ), Array( biquadraticIndexVariable, elementVariable ), Array() )
        val biquadraticConnectivityResource = region.createTextInlineResource( "test.biquadratic_connectivity.resource", "" )
        val biquadraticConnectivityData = region.createArrayDataSource( "test.biquadratic_connectivity.data", parameterResource, "1", 2 )
        biquadraticConnectivityData.rawSizes = Array( 8, 9 )
        biquadraticConnectivityData.sizes = Array( 8, 9 )
        val biquadraticConnectivity = region.createParameterEvaluator( "test.biquadratic_connectivity", nodes, biquadraticConnectivityData, biquadraticConnectivityDesc )
        
        biquadraticConnectivity( 1 ) = ( 1, 17, 2, 20, 21, 22, 5, 27, 6 ) 
        biquadraticConnectivity( 2 ) = ( 2, 18, 3, 22, 23, 24, 6, 28, 7 ) 
        biquadraticConnectivity( 3 ) = ( 3, 19, 4, 24, 25, 26, 7, 29, 8 ) 
        biquadraticConnectivity( 4 ) = ( 5, 27, 6, 30, 31, 32, 9, 36, 10 ) 
        biquadraticConnectivity( 6 ) = ( 7, 29, 8, 33, 34, 35, 11, 38, 12 ) 
        biquadraticConnectivity( 7 ) = ( 9, 36, 10, 39, 40, 41, 13, 46, 14 ) 
        biquadraticConnectivity( 8 ) = ( 10, 37, 11, 41, 42, 43, 14, 47, 15 ) 
        biquadraticConnectivity( 9 ) = ( 11, 38, 12, 43, 44, 45, 15, 48, 16 ) 

        val piecewise = region.createPiecewiseEvaluator( "test.piecewise", elementVariable, realType )
//        piecewise.setDefault( biquadraticInterpolator )
        piecewise.map( 1 -> biquadraticInterpolator )
        piecewise.map( 2 -> biquadraticInterpolator )
        piecewise.map( 3 -> biquadraticInterpolator )
        piecewise.map( 4 -> biquadraticInterpolator )
        piecewise.map( 5 -> bilinearInterpolator )
        piecewise.map( 6 -> biquadraticInterpolator )
        piecewise.map( 7 -> biquadraticInterpolator )
        piecewise.map( 8 -> biquadraticInterpolator )
        piecewise.map( 9 -> biquadraticInterpolator )
        
        println( "*** piecewise(?) = " + region.evaluate( piecewise ) )
        
        val bilinearParameters = region.createAggregateEvaluator( "test.bilinear_parameters", bilinearParametersType ) 
        bilinearParameters.bind_index( 1 -> bilinearIndexVariable )
        bilinearParameters.bind( nodesVariable -> bilinearConnectivity )
        bilinearParameters.setDefault( parameters )

        val biquadraticParameters = region.createAggregateEvaluator( "test.biquadratic_parameters", biquadraticParametersType ) 
        biquadraticParameters.bind_index( 1 -> biquadraticIndexVariable )
        biquadraticParameters.bind( nodesVariable -> biquadraticConnectivity )
        biquadraticParameters.setDefault( parameters )

        piecewise.bind( biquadraticParametersVariable -> biquadraticParameters )
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
        
        val f = new FileWriter( "collada nine quads.xml" )
        f.write( colladaXml )
        f.close()

        val json = JSONExporter.export2DFromFieldML( region, 8, "test.mesh", "test.aggregate" )
        
        val jsonFile = new FileWriter( "WebGL/test3_biquad.json" )
        jsonFile.write( json )
        jsonFile.close()
    }
}
