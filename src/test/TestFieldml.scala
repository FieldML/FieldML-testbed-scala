package test

import scala.collection.mutable.ArrayBuffer

import fieldml._
import fieldml.domain._
import fieldml.domain.bounds._

import fieldml.evaluator._

import framework.value.Value
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

        val index = region.createEnsembleDomain( "test.index", 4, false )
        
        val xi2dDomain : ContinuousDomain = library.getObject( "library.xi.2d" )
        val bilinearParameters : ContinuousDomain = library.getObject( "library.parameters.bilinear_lagrange" )
        
        region.set( xi2dDomain, 0.2, 0.2 )
        region.set( bilinearParameters, 1.0, 2.0, 3.0, 4.0 )
        
        val xi2dComponentDomain : EnsembleDomain = library.getObject( "library.ensemble.xi.2d" )

        val firstLocalxi2dDomain = region.createContinuousDomain( "test.domain.xi_2d_v1", xi2dComponentDomain )
        region.set( firstLocalxi2dDomain, 1.0, 1.0 )
        
        val secondLocalxi2dDomain = region.createContinuousDomain( "test.domain.xi_2d_v2", xi2dComponentDomain )
        region.set( secondLocalxi2dDomain, 0.333, 0.666 )

        val rawInterpolator = region.createReferenceEvaluator( "test.interpolator_v0", "library.fem.bilinear_lagrange", library, realDomain )

        println( "*** rawInterpolator(?) = " + region.getValue( rawInterpolator ) )

        val firstInterpolator = region.createReferenceEvaluator( "test.interpolator_v1", "library.fem.bilinear_lagrange", library, realDomain )
        firstInterpolator.alias( xi2dDomain -> firstLocalxi2dDomain )
        
        val secondInterpolator = region.createReferenceEvaluator( "test.interpolator_v2", "library.fem.bilinear_lagrange", library, realDomain )
        secondInterpolator.alias( xi2dDomain -> secondLocalxi2dDomain )
        
        val piecewise = region.createPiecewiseEvaluator( "test.piecewise", index, realDomain )
        piecewise.map( 1 -> firstInterpolator )
        piecewise.map( 2 -> secondInterpolator )
        
        println( "*** piecewise(?) = " + region.getValue( piecewise ) )
        
        region.set( index, 1 )
        
        println( "*** piecewise(1) = " + region.getValue( piecewise ) )
        
        region.set( index, 2 )
        
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        
        region.serialize()
    }
}
