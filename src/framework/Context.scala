package framework

import scala.collection.mutable.Map
import scala.collection.mutable.Stack

import fieldml._
import fieldml.domain._
import fieldml.evaluator._

import value._
import valuesource._

import util.exception._

class Context( val name : String )
{
    private val values = Map[FieldmlObject, ValueSource]()
    
    
    override def toString() : String =
    {
        return name + "[Context]"
    }
    

    def update( domain : Domain, value : Value )
    {
        values.get( domain ) match
        {
            case v : Some[ConstantValueSource] => add( new ConstantValueSource( domain, value ) )
            case None => add( new ConstantValueSource( domain, value ) )
            case _ => throw new FmlException( domain + " already has a value definition at this scope: " + values.get( domain ) )
        }
    }
    
    
    def add( valueSource : ValueSource )
    {
        values( valueSource.domain ) = valueSource
    }
    
    
    private def aliasEnsembleDomain( domain : EnsembleDomain, obj : FieldmlObject ) =
    {
        var domain2 : Domain = null
        
        obj match
        {
            case d : EnsembleDomain => domain2 = d
            case e : Evaluator => domain2 = e.valueDomain
        }
        
        if( domain2 == domain )
        {
            values( domain ) = new AliasValueSource( domain, obj )
        }
        else
        {
            throw new FmlInvalidObjectException( "Alias conflict: " + domain + " " + domain2 )
        }
    }

    
    private def aliasContinuousDomain( domain : ContinuousDomain, obj : FieldmlObject ) : Unit =
    {
        var domain2 : Domain = null
        
        obj match
        {
            case d : ContinuousDomain => domain2 = d
            case e : Evaluator => domain2 = e.valueDomain
        }
        
        if( domain2 == domain )
        {
            values( domain ) = new AliasValueSource( domain, obj )
            return
        }
        
        val componentDomain : EnsembleDomain = domain.componentDomain
        var componentDomain2 : EnsembleDomain = null
        
        domain2 match
        {
            case d: ContinuousDomain => componentDomain2 = d.componentDomain
            case e : EnsembleDomain => throw new FmlInvalidObjectException( "Alias conflict: " + domain + " " + domain2 )
        }
        
        if( componentDomain == componentDomain2 )
        {
            values( domain ) = new AliasValueSource( domain, obj )
        }
        else if( ( componentDomain != null ) && ( componentDomain2 == null ) )
        {
            values( domain ) = new VectorizedAliasValueSource( domain, componentDomain, obj )
        }
        else
        {
            throw new FmlInvalidObjectException( "Alias conflict: " + domain + " " + domain2 )
        }
    }

    
    def alias( domain : Domain, obj : FieldmlObject ) =
    {
        domain match
        {
            case e : EnsembleDomain => aliasEnsembleDomain( e, obj )
            case c : ContinuousDomain => aliasContinuousDomain( c, obj )
        }
    }

    
    def apply( domain : FieldmlObject ) : Option[ValueSource] =
    {
        return values.get( domain )
    }
}
