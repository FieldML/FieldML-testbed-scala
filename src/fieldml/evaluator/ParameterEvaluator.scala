package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.domain.Domain
import fieldml.domain.EnsembleDomain

import fieldml.evaluator.datastore.DataLocation
import fieldml.evaluator.datastore.DataDescription

import framework.value._

class ParameterEvaluator( name : String, valueDomain : Domain, var location : DataLocation, var description : DataDescription )
    extends Evaluator( name, valueDomain )
{
    private val map = Map[Array[Int], Value]()
    
    
    private def findKey( indexes : Array[Int] ) : Option[Array[Int]] =
    {
        for( k <- map.keys )
        {
            if( k.sameElements( indexes ) )
            {
                return Some( k )
            }
        }
        
        return None
    }
    
    
    def update( indexes : Array[Int], value : Value )
    {
        findKey( indexes ) match
        {
            case s : Some[Array[Int]] => map( s.get ) = value
            case None => { map( indexes.clone ) = value }
        }
    }
    
    
    def update( indexes : Array[Int], value : Int )
    {
        update( indexes, new EnsembleValue( value ) )
    }
    
    
    def update( index1 : Int, value : Int )
    {
        update( Array[Int]( index1 ), new EnsembleValue( value ) )
    }
    
    
    def update( index1 : Int, index2 : Int, value : Int )
    {
        update( Array[Int]( index1, index2 ), new EnsembleValue( value ) )
    }
    
    
    def update( indexes : Array[Int], values : Double* )
    {
        update( indexes, new ContinuousValue( values.toArray ) )
    }
    
    
    def update( index1 : Int, values : Double* )
    {
        update( Array[Int]( index1 ), new ContinuousValue( values.toArray ) )
    }
    
    
    def update( index1 : Int, index2 : Int, values : Double* )
    {
        update( Array[Int]( index1, index2 ), new ContinuousValue( values.toArray ) )
    }
    
    
    def apply( indexes : Array[Int] ) : Option[Value] =
    {
        findKey( indexes ) match
        {
            case s : Some[Array[Int]] => return map.get( s.get )
            case None => return None
        }
    }
    
    
    def apply( index1 : Int ) : Option[Value] =
    {
        return apply( Array[Int]( index1 ) )
    }
    
    
    def apply( index1 : Int, index2 : Int ) : Option[Value] =
    {
        return apply( Array[Int]( index1, index2 ) )
    }
}
