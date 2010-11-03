package framework.io.serialize

import fieldml.valueType.ContinuousType

import fieldml.evaluator.ReferenceEvaluator

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

class ReferenceEvaluatorSerializer( evaluator : ReferenceEvaluator )
{
    def insert( handle : Long ) : Unit =
    {
        val remoteHandle = GetNamedObject( handle, evaluator.refEvaluator.name )
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        var objectHandle = 0
        
        evaluator.valueType match
        {
            case d : ContinuousType => objectHandle = Fieldml_CreateContinuousReference( handle, evaluator.name, remoteHandle, valueHandle )
            case _ => println( "Cannot yet serialize " + evaluator )
        }
        
        for( pair <- evaluator.binds )
        {
            val valueTypeHandle = GetNamedObject( handle, pair._1.name )
            val sourceHandle = GetNamedObject( handle, pair._2.name )
            
            Fieldml_SetAlias( handle, objectHandle, valueTypeHandle, sourceHandle )
        }
    }
}