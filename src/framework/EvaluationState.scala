package framework

import scala.collection.mutable.Stack

import fieldml.FieldmlObject
import fieldml.domain.ContinuousDomain
import fieldml.domain.EnsembleDomain

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
    
    
    def get( obj : ContinuousDomain ) : Option[ContinuousValue] =
    {
        getValue( obj ) match
        {
            case v : Some[ContinuousValue] => return v
            case _ => return None
        }
    }
    
    
    def get( obj : EnsembleDomain ) : Option[EnsembleValue] =
    {
        getValue( obj ) match
        {
            case v : Some[EnsembleValue] => return v
            case _ => return None
        }
    }
}