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

object BiquadraticTest
{
    def main( argv : Array[String] ) : Unit =
    {
        val library = UserRegion.library
        
        val region = new UserRegion( "test" )

        val realType : ContinuousType = library.getObject( "library.real.1d" )
        val real3Type : ContinuousType = library.getObject( "library.real.3d" )
    
        val rc3ensemble : EnsembleType = library.getObject( "library.ensemble.rc.3d" )
        val real3IndexVariable : AbstractEvaluator = library.getCompanionVariable( rc3ensemble )
       
        val xi2dType : ContinuousType = library.getObject( "library.xi.2d" )
        val xi2dVar : AbstractEvaluator = library.getCompanionVariable( xi2dType )

        val meshType = region.createMeshType( "test.mesh.type", 9, xi2dType.componentType )
        val meshVariable = region.createAbstractEvaluator( "test.mesh", meshType )
        val elementVariable = region.createSubtypeEvaluator( meshVariable, "element" )
        val xiVariable = region.createSubtypeEvaluator( meshVariable, "xi" )

        val nodes = region.createEnsembleType( "test.nodes.type", 48, false )
        val nodesVariable = region.createAbstractEvaluator( "test.nodes", nodes )
        
        val bilinearParametersType : ContinuousType = library.getObject( "library.parameters.bilinear_lagrange" )
        val bilinearParametersVariable = library.getCompanionVariable( bilinearParametersType )
        val bilinearIndexVariable = library.getCompanionVariable( bilinearParametersType.componentType )
        
        val biquadraticParametersType : ContinuousType = library.getObject( "library.parameters.biquadratic_lagrange" )
        val biquadraticParametersVariable = library.getCompanionVariable( biquadraticParametersType )
        val biquadraticIndexVariable = library.getCompanionVariable( biquadraticParametersType.componentType )
        
        val bilinearInterpolator = region.createReferenceEvaluator( "test.bilinear_interpolator", "library.fem.bilinear_lagrange", library, realType )
        bilinearInterpolator.bind( xi2dVar -> xiVariable )
        
        val biquadraticInterpolator = region.createReferenceEvaluator( "test.biquadratic_interpolator", "library.fem.biquadratic_lagrange", library, realType )
        biquadraticInterpolator.bind( xi2dVar -> xiVariable )
        
        val parameterDescription = new SemidenseDataDescription( realType, Array( nodesVariable, real3IndexVariable ), Array() )
        val parameterLocation = new InlineDataLocation()
        val parameters = region.createParameterEvaluator( "test.parameters", realType, parameterLocation, parameterDescription )
        
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
        
        val bilinearElementSet = region.createElementSet( "test.bilinear_elements", elementVariable.valueType, 5 )
        val bilinearParameterSet = bilinearParametersType.componentType.elementSet
        
        val bilinearConnectivityDesc = new SemidenseDataDescription( nodes, Array( bilinearElementSet, bilinearParameterSet ), Array( elementVariable, bilinearIndexVariable ), Array() )
        val bilinearConnectivityLoc = new InlineDataLocation()
        val bilinearConnectivity = region.createParameterEvaluator( "test.bilinear_connectivity", nodes, bilinearConnectivityLoc, bilinearConnectivityDesc )
        
        bilinearConnectivity( 5 ) = ( 6, 7, 10, 11 ) 

        val biquadraticElementSet = region.createElementSet( "test.biquadratic_elements", elementVariable.valueType, 1, 2, 3, 4, 6, 7, 8, 9 )
        val biquadraticParameterSet = biquadraticParametersType.componentType.elementSet
        
        val biquadraticConnectivityDesc = new SemidenseDataDescription( nodes, Array( biquadraticElementSet, biquadraticParameterSet ), Array( elementVariable, biquadraticIndexVariable ), Array() )
        val biquadraticConnectivityLoc = new InlineDataLocation()
        val biquadraticConnectivity = region.createParameterEvaluator( "test.biquadratic_connectivity", nodes, biquadraticConnectivityLoc, biquadraticConnectivityDesc )
        
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
        
        val colladaXml = ColladaExporter.exportFromFieldML( region, 8, "test.mesh", "test.aggregate" )
        
        val f = new FileWriter( "collada nine quads.xml" )
        f.write( colladaXml )
        f.close()

        region.serialize()
    }
}
