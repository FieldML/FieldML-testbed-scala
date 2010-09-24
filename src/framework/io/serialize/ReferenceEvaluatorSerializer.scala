package framework.io.serialize

import fieldml.domain.ContinuousDomain

import fieldml.evaluator.ReferenceEvaluator

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

class ReferenceEvaluatorSerializer( evaluator : ReferenceEvaluator )
{
    def insert( handle : Long ) : Unit =
    {
        val remoteHandle = GetNamedObject( handle, evaluator.refEvaluator.name )
        val valueHandle = GetNamedObject( handle, evaluator.valueDomain.name )
        
        var objectHandle = 0
        
        evaluator.valueDomain match
        {
            case d : ContinuousDomain => objectHandle = Fieldml_CreateContinuousReference( handle, evaluator.name, remoteHandle, valueHandle )
            case _ => println( "Cannot yet serialize " + evaluator )
        }
        
        for( pair <- evaluator.aliases )
        {
            val domainHandle = GetNamedObject( handle, pair._1.name )
            val sourceHandle = GetNamedObject( handle, pair._2.name )
            
            Fieldml_SetAlias( handle, objectHandle, domainHandle, sourceHandle )
        }
    }
}