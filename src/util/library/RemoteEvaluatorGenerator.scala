package util.library

import scala.collection.mutable.Map

import fieldml.evaluator._
import fieldml.domain._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType._
import fieldml.jni.DataLocationType._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType

import util.exception._
import util.region._
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

        val scalarRealDomain : ContinuousDomain = region.getObject( "library.real.1d" )
        val xiDomains = Array[ContinuousDomain](
            null,
            region.getObject( "library.xi.1d" ),
            region.getObject( "library.xi.2d" ),
            region.getObject( "library.xi.3d" )
            )
        val paramDomains = Array[ContinuousDomain](
            null,
            region.getObject( "library.parameters.linear_lagrange" ),
            region.getObject( "library.parameters.bilinear_lagrange" ),
            region.getObject( "library.parameters.trilinear_lagrange" )
            )
        
        var d = 0
        name match
        {
            case "library.fem.linear_lagrange" => d = 1
            case "library.fem.bilinear_lagrange" => d = 2
            case "library.fem.trilinear_lagrange" => d = 3
            case _ => return null
        }
        
        val function =  new LinearLagrange( d )
        val foo = function.evaluate _

        return region.createFunctionEvaluator( name, foo, xiDomains( d ), paramDomains( d ), scalarRealDomain )
    }
}
