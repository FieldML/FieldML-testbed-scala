package framework.valuesource

import fieldml.evaluator.ParameterEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import framework.value.Value
import framework.Context
import framework.EvaluationState

import framework.datastore._

class ParameterEvaluatorValueSource( name : String, valueType : ValueType, dataStore : DataStore )
    extends ParameterEvaluator( name, valueType, dataStore )
    with ValueSource
{
    private val indexes = new Array[Int]( dataStore.description.indexEvaluators.size )

    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        val indexes = for( 
            eval <- dataStore.description.indexEvaluators;
            value <- eval.evaluate( state ) ) 
                yield value.eValue
        
        println( "Evaluating " + name + " at " + indexes.foldLeft( "" )( _ + " " + _ ) )
        
        if( dataStore.description.indexEvaluators.size != indexes.size )
        {
            return None
        }
        
        dataStore.description( indexes )
    }
}
