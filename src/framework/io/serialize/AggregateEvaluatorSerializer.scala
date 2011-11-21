package framework.io.serialize

import fieldml.evaluator.AggregateEvaluator

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

import framework.valuesource.AggregateEvaluatorValueSource

object AggregateEvaluatorSerializer
{
    def insert( handle : Int, evaluator : AggregateEvaluator ) : Unit =
    {
        val indexHandle = GetNamedObject( handle, evaluator.indexBinds( 1 ).name )
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        val objectHandle = Fieldml_CreateAggregateEvaluator( handle, evaluator.name, valueHandle )
        
        Fieldml_SetIndexEvaluator( handle, objectHandle, 1, indexHandle )
        
        if( evaluator.componentEvaluators.hasDefault )
        {
            val defaultEval = evaluator.componentEvaluators.default.get
            val defaultHandle = GetNamedObject( handle, defaultEval.name )
            Fieldml_SetDefaultEvaluator( handle, objectHandle, defaultHandle )
        }
        
        for( pair <- evaluator.componentEvaluators )
        {
            val evalHandle = GetNamedObject( handle, pair._2.name )
            Fieldml_SetEvaluator( handle, objectHandle, pair._1, evalHandle )
        }
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : AggregateEvaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        val valueType = source.getContinuousType( typeHandle )
        
        val aggEval = new AggregateEvaluatorValueSource( name, valueType )
        
        val indexEval = Fieldml_GetIndexEvaluator( source.fmlHandle, objectHandle, 1 )
        aggEval.bind_index( 1 -> source.getArgumentOrSubtypeEvaluator( indexEval ) )
        
        val defaultEval = Fieldml_GetDefaultEvaluator( source.fmlHandle, objectHandle )
        if( defaultEval != FML_INVALID_HANDLE )
        {
            aggEval.setDefault( source.getEvaluator( defaultEval ) )
        }
        
        val evalCount = Fieldml_GetEvaluatorCount( source.fmlHandle, objectHandle )
        for( i <- 1 to evalCount )
        {
            val element = Fieldml_GetEvaluatorElement( source.fmlHandle, objectHandle, i )
            val evaluator = Fieldml_GetEvaluator( source.fmlHandle, objectHandle, i )
            if( ( element > 0 ) && ( evaluator != FML_INVALID_HANDLE ) )
            {
                aggEval.map( element -> source.getEvaluator( evaluator ) )
            }
        }
        
        for( b <- GetBinds( source, objectHandle ) ) aggEval.bind( b )
        
        aggEval
    }
}
