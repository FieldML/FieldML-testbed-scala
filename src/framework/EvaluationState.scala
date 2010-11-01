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

    def push( context : Context )
    {
        stack.push( context )
    }
    
    
    def pop()
    {
        stack.pop()
    }
    
    
    private def getValue( obj : FieldmlObject ) : Option[Value] =
    {
        for( context <- stack;
            source <- context( obj )
            )
        {
            return source.getValue( this )
        }
        
        return None
    }
    
    
    def get( obj : FieldmlObject ) : Option[Value] =
    {
        return getValue( obj )
    }
    
    
    def get( obj : ContinuousType ) : Option[ContinuousValue] =
    {
        getValue( obj ) match
        {
            case v : Some[ContinuousValue] => return v
            case _ => return None
        }
    }
    
    
    def get( obj : EnsembleType ) : Option[EnsembleValue] =
    {
        getValue( obj ) match
        {
            case v : Some[EnsembleValue] => return v
            case _ => return None
        }
    }
    
    
    def getOrElse( evaluator : Evaluator, default : Int ) : Int =
    {
        getValue( evaluator ) match
        {
            case v : Some[EnsembleValue] => return v.get.value
            case _ => return default
        }
    }
    
    
    def getBind( evaluator : AbstractEvaluator ) : Evaluator =
    {
         for( context <- stack;
            source <- context.getBind( evaluator )
            )
        {
            return source
        }
        
        return null
   }
}