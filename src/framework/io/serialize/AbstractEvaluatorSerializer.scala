package framework.io.serialize

import fieldml.evaluator.AbstractEvaluator
import fieldml.valueType.ValueType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.TypeBoundsType
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

import framework.valuesource.AbstractEvaluatorValueSource


object AbstractEvaluatorSerializer
{
    def insert( handle : Long, evaluator : AbstractEvaluator ) : Unit =
    {
        val typeHandle = GetNamedObject( handle, evaluator.valueType.name )
        val objectHandle = Fieldml_CreateAbstractEvaluator( handle, evaluator.name, typeHandle )
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : AbstractEvaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        
        if( typeHandle < 0 ) println( name + " has no type" )
        
        val valueType = source.getType( typeHandle )
        
        new AbstractEvaluatorValueSource( name, valueType )
    }
}
