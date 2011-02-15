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
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.linear_lagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.bilinear_lagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.trilinear_lagrange.variable" ) )
            )
        val quadraticParamVariables = Array[Evaluator](
            null,
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.quadratic_lagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.biquadratic_lagrange.variable" ) ),
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.triquadratic_lagrange.variable" ) ),
            null
            )
            
        val xi1C_xi20_xi3N_swizzle = new ParamSwizzleValueSource( "xi1C_xi20_xi3N_swizzle", quadraticParamVariables(3).valueType,
            source.getAbstractEvaluator( Fieldml_GetObjectByName( source.fmlHandle, "library.parameters.triquadratic_lagrange_xi1C_xi20.variable" ) ),
            Array[Int](
                1, 1, 1, 2, 3, 4, 5, 6, 7,
                8, 8, 8, 9, 10, 11, 12, 13, 14,
                15, 15, 15, 16, 17, 18, 19, 20, 21
                ) )
        
        quadraticParamVariables( 4 ) = xi1C_xi20_xi3N_swizzle
        
        val fparams =
        name match
        {
            case "library.fem.linear_lagrange" => ( new LinearLagrange( 1 ).evaluate _, xiVariables( 1 ), linearParamVariables( 1 ) )
            case "library.fem.bilinear_lagrange" => ( new LinearLagrange( 2 ).evaluate _, xiVariables( 2 ), linearParamVariables( 2 ) )
            case "library.fem.trilinear_lagrange" => ( new LinearLagrange( 3 ).evaluate _, xiVariables( 3 ), linearParamVariables( 3 ) )
            case "library.fem.quadratic_lagrange" => ( new QuadraticLagrange( 1 ).evaluate _, xiVariables( 1 ), quadraticParamVariables( 1 ) )
            case "library.fem.biquadratic_lagrange" => ( new QuadraticLagrange( 2 ).evaluate _, xiVariables( 2 ), quadraticParamVariables( 2 ) )
            case "library.fem.triquadratic_lagrange" => ( new QuadraticLagrange( 3 ).evaluate _, xiVariables( 3 ), quadraticParamVariables( 3 ) )
            case "library.fem.triquadratic_lagrange_xi1C_xi20" => ( new QuadraticLagrange( 3 ).evaluate _, xiVariables( 3 ), quadraticParamVariables( 4 ) )
            case _ => return null
        }
        
        return new FunctionEvaluatorValueSource( name, fparams._1, fparams._2, fparams._3, scalarRealType )
    }
}
