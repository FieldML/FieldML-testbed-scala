package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.domain._
import fieldml.FieldmlObject

import framework.value._
import framework.Context
import framework.EvaluationState

class VectorizedAliasValueSource( sourceDomain : ContinuousDomain, val componentDomain : EnsembleDomain, val target : FieldmlObject )
    extends ValueSource( sourceDomain )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        val localContext = new Context( this.toString )
        
        state.push( localContext )

        val value = new Array[Double]( componentDomain.bounds.elementCount )
        for( e <- 1 to componentDomain.bounds.elementCount )
        {
            localContext( componentDomain ) = new EnsembleValue( e )
            state.get( target ) match
            {
                case c : Some[ContinuousValue] => value( e - 1 ) = c.get.value(0)
                case _ => //Should never happen
            }
        }
        return Some( new ContinuousValue( value ) )
    }
    
    
    override def toString() : String =
    {
        return "(" + domain + " -> " + target + ")[Alias]"
    }
}
