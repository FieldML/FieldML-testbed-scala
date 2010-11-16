package framework.datastore

import scala.collection.mutable.Map

import fieldml.evaluator.Evaluator
import fieldml.ElementSet
import fieldml.valueType.ValueType
import fieldml.valueType.EnsembleType

import framework.value.Value

class SemidenseDataDescription( valueType : ValueType, val denseSets : Array[ElementSet], val denseIndexes : Array[Evaluator], val sparseIndexes : Array[Evaluator] )
    extends DataDescription( valueType )
{
    def this( valueType : ValueType, denseIndexes : Array[Evaluator], sparseIndexes : Array[Evaluator] ) =
    {
        this( valueType, denseIndexes.map( _.valueType.asInstanceOf[EnsembleType].elementSet ), denseIndexes, sparseIndexes )
    }
    

    override def indexEvaluators : Array[Evaluator] = Array.concat( denseIndexes, sparseIndexes )


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
    
    
    def apply( indexes : Array[Int] ) : Option[Value] =
    {
        findKey( indexes ) match
        {
            case s : Some[Array[Int]] => return map.get( s.get )
            case None => return None
        }
    }
}
