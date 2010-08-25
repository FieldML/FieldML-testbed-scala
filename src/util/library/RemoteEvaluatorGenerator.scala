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
    def generateContinuousEvaluator( fmlHandle : Long, objectHandle : Int, region : Region ) :
        Option[ContinuousEvaluator] =
    {
        var continuousEvaluator : ContinuousEvaluator = null
        
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

        var scalarRealDomain : ContinuousDomain = region.getObject( "library.real.1d" )
        
        name match
        {
            case "library.fem.linear_lagrange" => return Some( new LinearLagrange( "library.fem.linear_lagrange", scalarRealDomain, 1 ) )
            case "library.fem.bilinear_lagrange" => return Some( new LinearLagrange( "library.fem.bilinear_lagrange", scalarRealDomain, 2 ) )
            case "library.fem.trilinear_lagrange" => return Some( new LinearLagrange( "library.fem.trilinear_lagrange", scalarRealDomain, 3 ) )
            case _ => return None
        }
    }
}
