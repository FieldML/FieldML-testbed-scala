package test

import scala.collection.mutable.ArrayBuffer

import fieldml._
import fieldml.domain._
import fieldml.domain.bounds._

import fieldml.evaluator._
import fieldml.evaluator.datastore._

import framework.value._
import framework._

import fieldml.jni.FieldmlApi._

import util._
import util.region._

object TestFieldml
{
    def main( args: Array[String] ): Unit =
    {
        val library = UserRegion.library
        
        val region = new UserRegion( "test" )

        val realDomain : ContinuousDomain = library.getObject( "library.real.1d" )
    
        val rc3ensemble : EnsembleDomain = library.getObject( "library.ensemble.rc.3d" )
       
        val rc3domain = region.createContinuousDomain( "test.domain.rc3" , rc3ensemble )

        val rc3ensemble2 : EnsembleDomain = library.getObject( "library.ensemble.rc.3d" )

        val rc3domain2 = region.createContinuousDomain( "test.domain.rc3_2" , rc3ensemble )
        
        val bilinearLagrange : FunctionEvaluator = library.getObject( "library.fem.bilinear_lagrange" )
        
        val xi2dDomain : ContinuousDomain = library.getObject( "library.xi.2d" )

        val mesh = region.createMeshDomain( "test.mesh", 2, xi2dDomain.componentDomain ) 

        val nodes = region.createEnsembleDomain( "test.nodes", 6, false )
        
        val bilinearParametersDomain : ContinuousDomain = library.getObject( "library.parameters.bilinear_lagrange" )
        val bilinearIndex = bilinearParametersDomain.componentDomain
        
        region.set( xi2dDomain, 0.2, 0.2 )
//        region.set( bilinearParametersDomain, 1.0, 2.0, 3.0, 4.0 )
        

        val rawInterpolator = region.createReferenceEvaluator( "test.interpolator_v0", "library.fem.bilinear_lagrange", library, realDomain )

//        println( "*** rawInterpolator(?) = " + region.getValue( rawInterpolator ) )

        val firstInterpolator = region.createReferenceEvaluator( "test.interpolator_v1", "library.fem.bilinear_lagrange", library, realDomain )
        firstInterpolator.alias( xi2dDomain -> mesh.xiDomain )
        
        val secondInterpolator = region.createReferenceEvaluator( "test.interpolator_v2", "library.fem.bilinear_lagrange", library, realDomain )
        secondInterpolator.alias( xi2dDomain -> mesh.xiDomain )
        
        val parameterDescription = new SemidenseDataDescription( Array( nodes ), Array() )
        val parameterLocation = new InlineDataLocation()
        val parameters = region.createParameterEvaluator( "test.parameters", realDomain, parameterLocation, parameterDescription )
        
        parameters( 1 ) = 1.0
        parameters( 2 ) = 1.5
        parameters( 3 ) = 2.0
        parameters( 4 ) = 2.5
        parameters( 5 ) = 3.0
        parameters( 6 ) = 3.5
        
        println( parameters( 3 ) )
        println( parameters( 2 ) )
        println( parameters( 1 ) )

        val connectivityDescription = new SemidenseDataDescription( Array( mesh.elementDomain, bilinearIndex ), Array() )
        val connectivityLocation = new InlineDataLocation()
        val connectivity = region.createParameterEvaluator( "test.connectivity", nodes, connectivityLocation, connectivityDescription )
        
        connectivity( 1, 1 ) = 1
        connectivity( 1, 2 ) = 4
        connectivity( 1, 3 ) = 2
        connectivity( 1, 4 ) = 5
        connectivity( 2, 1 ) = 2
        connectivity( 2, 2 ) = 5
        connectivity( 2, 3 ) = 3
        connectivity( 2, 4 ) = 6

        val piecewise = region.createPiecewiseEvaluator( "test.piecewise", mesh.elementDomain, realDomain )
        piecewise.map( 1 -> firstInterpolator )
        piecewise.map( 2 -> secondInterpolator )
        
        println( "*** piecewise(?) = " + region.getValue( piecewise ) )
        
        val bilinearParameters = region.createReferenceEvaluator( "test.bilinear_parameters", "test.parameters", region, realDomain ) 
        bilinearParameters.alias( nodes -> connectivity )
        
        piecewise.alias( bilinearParametersDomain -> bilinearParameters )
        
        region.set( mesh, 2, 0, 0 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )
        
        region.set( mesh, 2, 1, 0 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )

        region.set( mesh, 2, 0, 1 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )
        
        region.set( mesh, 2, 1, 1 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )

        region.serialize()
    }
}
