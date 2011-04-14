package framework

import scala.collection.mutable.Stack

import fieldml.FieldmlObject
import fieldml.evaluator.Evaluator

class EvaluationState
{
    private val stack = Stack[Context]()

    def pop()
    {
        stack.pop()
    }
    
    
    def pushAndApply( binds : Seq[Tuple2[Evaluator, Evaluator]] )
    {
        if( stack.size > 0 )
        {
            stack.push( new Context( stack( 0 ) ) )
        }
        else
        {
            stack.push( new Context() )
        }
        
        for( b <- binds )
        {
            stack.top.setBind( b._1, b._2 )
        }
    }
    
    
    def getBind( variable : Evaluator ) : Option[Evaluator] =
    {
        return stack( 0 ).getBind( variable )
    }
}