package framework

import scala.collection.mutable.Stack

import fieldml.FieldmlObject
import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType
import fieldml.evaluator.Evaluator
import fieldml.evaluator.AbstractEvaluator

import value.Value
import value.ContinuousValue
import value.EnsembleValue

class EvaluationState
{
    private val stack = Stack[Context]()

    def pop()
    {
        stack.pop()
    }
    
    
    def pushAndApply( binds : Seq[Tuple2[AbstractEvaluator, Evaluator]] )
    {
        stack.push( new Context() )
        for( b <- binds )
        {
            stack.top.setBind( b._1, b._2 )
        }
    }
    
    
    def getBind( variable : AbstractEvaluator ) : Option[Evaluator] =
    {
        for( context <- stack; evaluator <- context.getBind( variable ) )
        {
            return Some( evaluator )
        }
        
        return None
    }
}