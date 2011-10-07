package framework

import scala.collection.mutable.Stack

import fieldml.FieldmlObject
import fieldml.evaluator.Evaluator

class EvaluationState
{
    private val stack = Stack[Context]()


    private def printContext( depth : Int, c : Context )
    {
        for( i <- 0 until depth ) print( "  " )
        println( c.location )
    }

    
    def printStack
    {
        var depth = 0;
        for( s <- stack.toSeq )
        {
            printContext( depth, s )
            depth = depth + 1
        }
    }
    
    def pop()
    {
        stack.pop()
    }
    
    
    def pushAndApply( location : String, binds : Seq[Tuple2[Evaluator, Evaluator]] )
    {
        if( stack.size > 0 )
        {
            stack.push( new Context( location, stack( 0 ) ) )
        }
        else
        {
            stack.push( new Context( location ) )
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