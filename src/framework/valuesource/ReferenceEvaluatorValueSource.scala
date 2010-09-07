package framework.valuesource

import fieldml.evaluator.ReferenceEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ReferenceEvaluatorValueSource( private val evaluator : ReferenceEvaluator, private val refContext : Context )
    extends EvaluatorValueSource( evaluator )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        val localContext = new Context( evaluator.name )
        
        for( alias <- evaluator.aliases )
        {
            localContext.alias( alias._1, alias._2 )
        }
        
        state.push( localContext )
        state.push( refContext )
        
        val v = state.get( evaluator.refEvaluator )
        
        state.pop()
        state.pop()
        
        return v
    }
}
