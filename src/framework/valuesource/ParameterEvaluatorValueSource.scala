package framework.valuesource

import fieldml.evaluator.ParameterEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

import fieldml.evaluator.datastore._

class ParameterEvaluatorValueSource( private val evaluator : ParameterEvaluator )
    extends EvaluatorValueSource( evaluator )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        val keys = evaluator.description.indexDomains
        val indexes = keys.map( state.getOrElse( _, 0 ) )
        
        return evaluator( indexes )
    }
}
