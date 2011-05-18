package framework.io.serialize

import fieldml.evaluator.ArgumentEvaluator
import fieldml.valueType.ValueType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

import framework.valuesource.ArgumentEvaluatorValueSource


object ArgumentEvaluatorSerializer
{
    def insert( handle : Int, evaluator : ArgumentEvaluator ) : Unit =
    {
        val typeHandle = GetNamedObject( handle, evaluator.valueType.name )
        val objectHandle = Fieldml_CreateArgumentEvaluator( handle, evaluator.name, typeHandle )
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : ArgumentEvaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        
        if( typeHandle < 0 ) println( name + " has no type" )
        
        val valueType = source.getType( typeHandle )
        
        new ArgumentEvaluatorValueSource( name, valueType )
    }
}
