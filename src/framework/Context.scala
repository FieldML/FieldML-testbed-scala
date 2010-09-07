package framework

import scala.collection.mutable.Map
import scala.collection.mutable.Stack

import fieldml._
import fieldml.domain._

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

    
    def alias( domain1 : Domain, domain2 : FieldmlObject ) =
    {
        values( domain1 ) = new AliasValueSource( domain1, domain2 )
    }

    
    def apply( domain : FieldmlObject ) : Option[ValueSource] =
    {
        return values.get( domain )
    }
}
