package framework.valuesource

import scala.collection.mutable.Stack

import fieldml.domain.Domain
import fieldml.FieldmlObject

import framework.value.Value
import framework.value.EnsembleValue
import framework.value.ContinuousValue
import framework.value.MeshValue
import framework.Context
import framework.EvaluationState

class SubdomainValueSource( sourceDomain : Domain, val parentDomain : Domain, val component : String )
    extends ValueSource( sourceDomain )
{
    private def subdomainValue( value : MeshValue ) : Option[Value] =
    {
        component match
        {
            case "element" => return Some( new EnsembleValue( value.elementValue ) )
            case "xi" => return Some( new ContinuousValue( value.xiValue ) )
            case _ => return None
        }
    }
    
    
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        state.get( parentDomain ) match
        {
            case m : Some[MeshValue] => return subdomainValue( m.get )
            case _ => return None
        }
    }
    
    
    override def toString() : String =
    {
        return "(" + domain + " -> " + parentDomain + "." + component + ")[Alias]"
    }
}
