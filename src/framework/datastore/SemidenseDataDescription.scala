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
    

    override val indexEvaluators : Array[Evaluator] = Array.concat( denseIndexes, sparseIndexes )
    
    
    private val counts = indexEvaluators.map( _.valueType.asInstanceOf[EnsembleType].elementSet.size )

    
    private val isQuick = calcIsQuick
        
    private def calcIsQuick : Boolean =
    {
        var count : Long = 1
        
        for( e <- 1 until indexEvaluators.size )
        {
            if( count * counts(e) > Int.MaxValue )
            {
                return false
            }
        }
        
        return true
    }
    

    private val map = Map[Array[Int], Value]()
    
    private val qMap = Map[Int, Value]()


    private def getQHash( indexes : Array[Int] ) : Int =
    {
        var key = 0
        for( i <- 0 until counts.size )
        {
            key *= counts( i )
            key += indexes( i )
        }
        
        return key
    }
    

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
        if( indexes.size != indexEvaluators.size )
        {
            return
        }
        if( isQuick )
        {
            val key = getQHash( indexes )
            qMap( key ) = value
            return
        }
        
        findKey( indexes ) match
        {
            case s : Some[Array[Int]] => map( s.get ) = value
            case None => { map( indexes.clone ) = value }
        }
    }
    
    
    def apply( indexes : Array[Int] ) : Option[Value] =
    {
        if( indexes.size != indexEvaluators.size )
        {
            return None
        }
        if( isQuick )
        {
            val key = getQHash( indexes )
            return qMap.get( key )
        }
        
        findKey( indexes ) match
        {
            case s : Some[Array[Int]] => return map.get( s.get )
            case None => return None
        }
    }
}
