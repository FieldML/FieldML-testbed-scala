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
    private val indexes = Array[Int]( dataStore.description.indexEvaluators.size )

    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        for( i <- 0 until indexes.size )
        {
            dataStore.description.indexEvaluators( i ).evaluate( state ) match
            {
                case s : Some[Value] => indexes( i ) = s.get.eValue
                case None => println( "Abstract evaluator " + name + " is not bound" ); return None
            }
        }
        
        return dataStore.description.apply( indexes )
    }
}
