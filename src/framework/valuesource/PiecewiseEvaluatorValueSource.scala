package framework.valuesource

import fieldml.evaluator.PiecewiseEvaluator

import framework.value.Value
import framework.Context
import framework.EvaluationState

class PiecewiseEvaluatorValueSource( private val evaluator : PiecewiseEvaluator )
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
        
        for(
            key <- state.get( evaluator.index );
            eval <- evaluator.delegations.get( key.value );
            v <- state.get( eval )
            )
        {
            state.pop()
            return Some(v)
        }

        state.pop()
        return None
    }

}
