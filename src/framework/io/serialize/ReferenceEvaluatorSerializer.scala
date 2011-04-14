package framework.io.serialize

import fieldml.evaluator.ReferenceEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion
import framework.valuesource.ReferenceEvaluatorValueSource


object ReferenceEvaluatorSerializer
{
    def insert( handle : Int, evaluator : ReferenceEvaluator ) : Unit =
    {
        val remoteHandle = GetNamedObject( handle, evaluator.refEvaluator.name )
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        var objectHandle = Fieldml_CreateReferenceEvaluator( handle, evaluator.name, remoteHandle );
        
        for( pair <- evaluator.binds )
        {
            val valueTypeHandle = GetNamedObject( handle, pair._1.name )
            val sourceHandle = GetNamedObject( handle, pair._2.name )
            
            Fieldml_SetBind( handle, objectHandle, valueTypeHandle, sourceHandle )
        }
    }

    
    def extract( source : Deserializer, objectHandle : Int ) :
        ReferenceEvaluator = 
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        val remoteHandle = Fieldml_GetReferenceSourceEvaluator( source.fmlHandle, objectHandle )
        
        val evaluator : Evaluator = source.getEvaluator( remoteHandle )
        
        val refEval = new ReferenceEvaluatorValueSource( name, evaluator )
        
        for( b <- GetBinds( source, objectHandle ) ) refEval.bind( b )
        
        refEval
    }
}
