package framework

import scala.collection.mutable.Stack

import fieldml.FieldmlObject
import fieldml.evaluator.Evaluator
import fieldml.evaluator.ArgumentEvaluator

import framework.value.Value
import framework.valuesource.ValueSource

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
        for( s <- stack.toSeq.reverse )
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
            stack.push( new Context( location, Some( stack.top ), binds ) )
        }
        else
        {
            stack.push( new Context( location, None, binds ) )
        }
    }
    
    
    def getBind( variable : Evaluator ) : Option[Evaluator] =
    {
        return stack.top.getBind( variable )
    }
    
    
    private def restart( argument : Evaluator, binds : Seq[Tuple2[Evaluator, Evaluator]] ) : EvaluationState =
    {
        val newState = new EvaluationState()
        
        newState.stack.push( new Context( "TEMP for " + argument.name, stack.top.getBindContext( argument ), binds ) )
        
        return newState
    }
    
    
    def resolve( argument : ArgumentEvaluator, binds : Seq[Tuple2[Evaluator, Evaluator]] ) : Option[Value] =
    {
        if( getBind( argument ) == None )
        {
            None
        }
        else
        {
            val tempState = restart( argument, binds )
            
            tempState.getBind( argument ).flatMap( _.asInstanceOf[ValueSource].evaluate( tempState ) )
        }
    }
}
