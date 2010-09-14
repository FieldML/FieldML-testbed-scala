package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.domain.Domain
import fieldml.FieldmlObject

import framework.value.Value
import framework.Context
import framework.EvaluationState

class AliasValueSource( sourceDomain : Domain, val target : FieldmlObject )
    extends ValueSource( sourceDomain )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        return state.get( target )
    }
    
    
    override def toString() : String =
    {
        return "(" + domain + " -> " + target + ")[Alias]"
    }
}
