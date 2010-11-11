package util.library

import scala.collection.mutable.Map

import fieldml.evaluator._
import fieldml.valueType._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType._
import fieldml.jni.DataLocationType._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType

import framework.region._

import util.exception._
import util._

object RemoteEvaluatorGenerator
{
    def generateContinuousEvaluator( fmlHandle : Long, objectHandle : Int, region : UserRegion ) :
        Evaluator =
    {
        val name = Fieldml_GetObjectName( fmlHandle, objectHandle )
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        if( objectType != FHT_REMOTE_CONTINUOUS_EVALUATOR )
        {
            Fieldml_GetLastError( fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( "Object handle " + objectHandle + " is invalid" )
                case _ => throw new FmlTypeException( name, objectType, FHT_REMOTE_CONTINUOUS_EVALUATOR )
            }
        }

        val scalarRealType : ContinuousType = region.getObject( "library.real.1d" )
        val xiTypes = Array[ContinuousType](
            null,
            region.getObject( "library.xi.1d" ),
            region.getObject( "library.xi.2d" ),
            region.getObject( "library.xi.3d" )
            )
        val linearParamTypes = Array[ContinuousType](
            null,
            region.getObject( "library.parameters.linear_lagrange" ),
            region.getObject( "library.parameters.bilinear_lagrange" ),
            region.getObject( "library.parameters.trilinear_lagrange" )
            )
        val quadraticParamTypes = Array[ContinuousType](
            null,
            region.getObject( "library.parameters.quadratic_lagrange" ),
            region.getObject( "library.parameters.biquadratic_lagrange" ),
            region.getObject( "library.parameters.triquadratic_lagrange" )
            )
        
        var fparams =
        name match
        {
            case "library.fem.linear_lagrange" => ( new LinearLagrange( 1 ).evaluate _, xiTypes( 1 ), linearParamTypes( 1 ) )
            case "library.fem.bilinear_lagrange" => ( new LinearLagrange( 2 ).evaluate _, xiTypes( 2 ), linearParamTypes( 2 ) )
            case "library.fem.trilinear_lagrange" => ( new LinearLagrange( 3 ).evaluate _, xiTypes( 3 ), linearParamTypes( 3 ) )
            case "library.fem.quadratic_lagrange" => ( new QuadraticLagrange( 1 ).evaluate _, xiTypes( 1 ), quadraticParamTypes( 1 ) )
            case "library.fem.biquadratic_lagrange" => ( new QuadraticLagrange( 2 ).evaluate _, xiTypes( 2 ), quadraticParamTypes( 2 ) )
            case "library.fem.triquadratic_lagrange" => ( new QuadraticLagrange( 3 ).evaluate _, xiTypes( 3 ), quadraticParamTypes( 3 ) )
            case _ => return null
        }
        
        val arg1 = region.getCompanionVariable( fparams._2 )
        val arg2 = region.getCompanionVariable( fparams._3 )

        return region.createFunctionEvaluator( name, fparams._1, arg1, arg2, scalarRealType )
    }
}
