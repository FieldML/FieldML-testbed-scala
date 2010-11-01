package framework.datastore

import fieldml.evaluator.ParameterEvaluator
import framework.value._

class ParameterEvaluatorApplier( private val evaluator : ParameterEvaluator )
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
        update( indexes, Value( evaluator.valueType, value ) )
    }
    
    
    def update( index1 : Int, value : Int )
    {
        update( Array[Int]( index1 ), Value( evaluator.valueType, value ) )
    }
    
    
    def update( index1 : Int, index2 : Int, value : Int )
    {
        update( Array[Int]( index1, index2 ), Value( evaluator.valueType, value ) )
    }
    
    
    def update( indexes : Array[Int], values : Double* )
    {
        update( indexes, Value( evaluator.valueType, values: _* ) )
    }
    
    
    def update( index1 : Int, values : Double* )
    {
        update( Array[Int]( index1 ), Value( evaluator.valueType, values: _* ) )
    }
    
    
    def update( index1 : Int, index2 : Int, values : Double* )
    {
        update( Array[Int]( index1, index2 ), Value( evaluator.valueType, values: _* ) )
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