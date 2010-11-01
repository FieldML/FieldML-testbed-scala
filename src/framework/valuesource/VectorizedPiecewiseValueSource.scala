package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType._
import fieldml.FieldmlObject

import fieldml.evaluator.PiecewiseEvaluator

import framework.value._
import framework.Context
import framework.EvaluationState

class VectorizedPiecewiseValueSource( private val evaluator : PiecewiseEvaluator, private val outputType : ValueType )
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
        
        val value = new Array[Double]( evaluator.index.bounds.elementCount )
        for(
            key <- 1 to evaluator.index.bounds.elementCount;
            eval <- evaluator.delegations.get( key )
            )
        {
            localContext( evaluator.index ) = new EnsembleValue( evaluator.index, key )
            state.get( eval ) match
            {
                case c : Some[ContinuousValue] => value( key - 1 ) = c.get.value(0)
                case _ => return None
            }
            
        }

        state.pop()
        return Some( Value( outputType, value: _* ) )
    }
}
