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

import framework.valuesource.FunctionEvaluatorValueSource
import framework.valuesource.ParamSwizzleValueSource
import framework.region._
import framework.io.serialize.Deserializer

import util.exception._
import util._

object ExternalEvaluatorGenerator
{
    def generateContinuousEvaluator( source : Deserializer, objectHandle : Int ) :
        Evaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        val objectType = Fieldml_GetObjectType( source.fmlHandle, objectHandle )
        
        if( objectType != FHT_EXTERNAL_EVALUATOR )
        {
            Fieldml_GetLastError( source.fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( "Object handle " + objectHandle + " is invalid" )
                case _ => throw new FmlTypeException( name, objectType, FHT_EXTERNAL_EVALUATOR )
            }
        }

        val scalarRealType : ContinuousType = source.getContinuousType( Fieldml_GetObjectByName( source.fmlHandle, "library.real.1d" ) )
        val xiVariables = Array[AbstractEvaluator](
            null,
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.xi.1d.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.xi.2d.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.xi.3d.variable" ) )
            )
        val linearParamVariables = Array[AbstractEvaluator](
            null,
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.1d.linearLagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.2d.bilinearLagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.3d.trilinearLagrange.variable" ) )
            )
        val quadraticParamVariables = Array[Evaluator](
            null,
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.1d.quadraticLagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.2d.biquadraticLagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.3d.triquadraticLagrange.variable" ) ),
            null
            )
        val cubicHermiteParamVariables = Array[Evaluator](
            null,
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.1d.cubicHermite.variable" ) ),
            null,
            null,
            null
            )
            
        val fparams =
        name match
        {
            case "library.interpolator.1d.unit.cubicHermite" => ( new CubicHermite( 1 ).evaluate _, xiVariables( 1 ), cubicHermiteParamVariables( 1 ) )
            case "library.interpolator.1d.unit.linearLagrange" => ( new LinearLagrange( 1 ).evaluate _, xiVariables( 1 ), linearParamVariables( 1 ) )
            case "library.interpolator.2d.unit.bilinearLagrange" => ( new LinearLagrange( 2 ).evaluate _, xiVariables( 2 ), linearParamVariables( 2 ) )
            case "library.interpolator.3d.unit.trilinearLagrange" => ( new LinearLagrange( 3 ).evaluate _, xiVariables( 3 ), linearParamVariables( 3 ) )
            case "library.interpolator.1d.unit.quadraticLagrange" => ( new QuadraticLagrange( 1 ).evaluate _, xiVariables( 1 ), quadraticParamVariables( 1 ) )
            case "library.interpolator.2d.unit.biquadraticLagrange" => ( new QuadraticLagrange( 2 ).evaluate _, xiVariables( 2 ), quadraticParamVariables( 2 ) )
            case "library.interpolator.3d.unit.triquadraticLagrange" => ( new QuadraticLagrange( 3 ).evaluate _, xiVariables( 3 ), quadraticParamVariables( 3 ) )
            case _ => System.err.println( "Unknown external evaluator " + name ); return null
        }
        
        return new FunctionEvaluatorValueSource( name, fparams._1, fparams._2, fparams._3, scalarRealType )
    }
}
