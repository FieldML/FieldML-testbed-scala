package util.library

import scala.collection.mutable.Map

import fieldml.evaluator._
import fieldml.valueType._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType._
import fieldml.jni.DataSourceType._
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
        val name = Fieldml_GetObjectDeclaredName( source.fmlHandle, objectHandle )
        val objectType = Fieldml_GetObjectType( source.fmlHandle, objectHandle )
        
        if( objectType != FHT_EXTERNAL_EVALUATOR )
        {
            Fieldml_GetLastError( source.fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( "Object handle " + objectHandle + " is invalid" )
                case _ => throw new FmlTypeException( name, objectType, FHT_EXTERNAL_EVALUATOR )
            }
        }

        val evaluatorType : ContinuousType = source.getContinuousType( Fieldml_GetValueType( source.fmlHandle, objectHandle ) )
        val xiNames = Array[String](
            null,
            "chart.1d.argument",
            "chart.2d.argument",
            "chart.3d.argument"
            )
        val linearParamNames = Array[String](
            null,
            "parameters.1d.unit.linearLagrange.argument",
            "parameters.2d.unit.bilinearLagrange.argument",
            "parameters.3d.unit.trilinearLagrange.argument"
            )
        val quadraticParamNames = Array[String](
            null,
            "parameters.1d.unit.quadraticLagrange.argument",
            "parameters.2d.unit.biquadraticLagrange.argument",
            "parameters.3d.unit.triquadraticLagrange.argument",
            null
            )
        val cubicHermiteParamNames = Array[String](
            null,
            "parameters.1d.unit.cubicHermite.argument",
            null,
            null,
            null
            )

        val linearSimplexParamNames = Array[String](
            null,
            null, //NYI
            "parameters.2d.unit.bilinearSimplex.argument",
            null //NYI
            )

        val fparams =
        name match
        {
            case "interpolator.1d.unit.cubicHermite" => ( new CubicHermite( 1 ).evaluate _, xiNames( 1 ), cubicHermiteParamNames( 1 ) )
            case "interpolator.1d.unit.linearLagrange" => ( new LinearLagrange( 1 ).evaluate _, xiNames( 1 ), linearParamNames( 1 ) )
            case "interpolator.2d.unit.bilinearLagrange" => ( new LinearLagrange( 2 ).evaluate _, xiNames( 2 ), linearParamNames( 2 ) )
            case "interpolator.3d.unit.trilinearLagrange" => ( new LinearLagrange( 3 ).evaluate _, xiNames( 3 ), linearParamNames( 3 ) )
            case "interpolator.1d.unit.quadraticLagrange" => ( new QuadraticLagrange( 1 ).evaluate _, xiNames( 1 ), quadraticParamNames( 1 ) )
            case "interpolator.2d.unit.biquadraticLagrange" => ( new QuadraticLagrange( 2 ).evaluate _, xiNames( 2 ), quadraticParamNames( 2 ) )
            case "interpolator.3d.unit.triquadraticLagrange" => ( new QuadraticLagrange( 3 ).evaluate _, xiNames( 3 ), quadraticParamNames( 3 ) )
            case "interpolator.2d.unit.bilinearSimplex" => ( new BilinearSimplex().evaluate _, xiNames( 2 ), linearSimplexParamNames( 2 ) )
            case _ => System.err.println( "Unknown external evaluator " + name ); return null
        }
        
        val xiVariable = source.getArgumentEvaluator( Fieldml_GetObjectByDeclaredName( source.fmlHandle, fparams._2 ) )
        val phiVariable = source.getArgumentEvaluator( Fieldml_GetObjectByDeclaredName( source.fmlHandle, fparams._3 ) )
        
        val localName = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
    
        return new FunctionEvaluatorValueSource( localName, fparams._1, xiVariable, phiVariable, evaluatorType )
    }
}
