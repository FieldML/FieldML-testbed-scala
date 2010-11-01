package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.valueType._
import fieldml.FieldmlObject

import framework.value._
import framework.Context
import framework.EvaluationState

class VectorizedAliasValueSource( sourceType : ContinuousType, val componentType : EnsembleType, val target : FieldmlObject )
    extends ValueSource( sourceType )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        val localContext = new Context( this.toString )
        
        state.push( localContext )

        val value = new Array[Double]( componentType.bounds.elementCount )
        for( e <- 1 to componentType.bounds.elementCount )
        {
            localContext( componentType ) = Value( componentType, e )
            state.get( target ) match
            {
                case c : Some[ContinuousValue] => value( e - 1 ) = c.get.value(0)
                case _ => //Should never happen
            }
        }
        return Some( Value( sourceType, value: _* ) )
    }
    
    
    override def toString() : String =
    {
        return "(" + valueType + " -> " + target + ")[Alias]"
    }
}
