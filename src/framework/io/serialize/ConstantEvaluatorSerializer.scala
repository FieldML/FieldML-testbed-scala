package framework.io.serialize

import fieldml.evaluator.ConstantEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion
import framework.valuesource.ConstantEvaluatorValueSource


object ConstantEvaluatorSerializer
{
    def insert( handle : Int, evaluator : ConstantEvaluator ) : Unit =
    {
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        var objectHandle = Fieldml_CreateConstantEvaluator( handle, evaluator.name, evaluator.valueString, valueHandle );
    }

    
    def extract( source : Deserializer, objectHandle : Int ) :
        ConstantEvaluator = 
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        
        val valueString = Fieldml_GetConstantEvaluatorValueString( source.fmlHandle, objectHandle )
        
        val valueType : ValueType = source.getType( typeHandle )
        
        val constantEval = new ConstantEvaluatorValueSource( name, valueString, valueType )
        
        constantEval
    }
}
