package framework.io.serialize

import fieldml.evaluator.PiecewiseEvaluator

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

import framework.valuesource.PiecewiseEvaluatorValueSource

object PiecewiseEvaluatorSerializer
{
    def insert( handle : Int, evaluator : PiecewiseEvaluator ) : Unit =
    {
        val indexHandle = GetNamedObject( handle, evaluator.index.name )
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        val objectHandle = Fieldml_CreatePiecewiseEvaluator( handle, evaluator.name, valueHandle )
        
        Fieldml_SetIndexEvaluator( handle, objectHandle, 1, indexHandle )
        
        if( evaluator.delegations.hasDefault )
        {
            val defaultEval = evaluator.delegations.default.get
            val defaultHandle = GetNamedObject( handle, defaultEval.name )
            Fieldml_SetDefaultEvaluator( handle, objectHandle, defaultHandle )
        }
        
        for( pair <- evaluator.delegations )
        {
            val evalHandle = GetNamedObject( handle, pair._2.name )
            Fieldml_SetEvaluator( handle, objectHandle, pair._1, evalHandle )
        }
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : PiecewiseEvaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        val valueType = source.getContinuousType( typeHandle )
        
        val indexEvalHandle = Fieldml_GetIndexEvaluator( source.fmlHandle, objectHandle, 1 )
        val indexEval = source.getEvaluator( indexEvalHandle )

        val piecewiseEval = new PiecewiseEvaluatorValueSource( name, valueType, indexEval )
        
        val defaultEval = Fieldml_GetDefaultEvaluator( source.fmlHandle, objectHandle )
        if( defaultEval != FML_INVALID_HANDLE )
        {
            piecewiseEval.setDefault( source.getEvaluator( defaultEval ) )
        }

        val evalCount = Fieldml_GetEvaluatorCount( source.fmlHandle, objectHandle )
        for( i <- 1 to evalCount )
        {
            val element = Fieldml_GetEvaluatorElement( source.fmlHandle, objectHandle, i )
            val evaluator = Fieldml_GetEvaluator( source.fmlHandle, objectHandle, i )
            if( ( element > 0 ) && ( evaluator != FML_INVALID_HANDLE ) )
            {
                piecewiseEval.map( element -> source.getEvaluator( evaluator ) )
            }
        }
        
        for( b <- GetBinds( source, objectHandle ) ) piecewiseEval.bind( b )
        
        piecewiseEval
    }
}
