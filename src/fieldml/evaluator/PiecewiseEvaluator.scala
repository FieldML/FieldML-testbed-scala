package fieldml.evaluator

import scala.collection.mutable.Map 

import util.DefaultingHashMap

import fieldml.valueType.EnsembleType
import fieldml.valueType.ValueType
import fieldml.FieldmlObject

import framework.value.EnsembleValue

class PiecewiseEvaluator( name : String, valueType : ValueType, val index : EnsembleType )
    extends Evaluator( name, valueType )
{
    val delegations = new DefaultingHashMap[Int, Evaluator]()
    
    val aliases = Map[ ValueType, FieldmlObject ]()
    
    def variables = delegations.values.flatMap( _.variables )

    def alias( alias : Tuple2[ ValueType, FieldmlObject ] )
    {
        aliases( alias._1 ) = alias._2
    }

    
    def map( pair : Tuple2[ Int, Evaluator] )
    {
        delegations( pair._1 ) = pair._2
    }
    
    
    def setDefault( default : Evaluator )
    {
        delegations.default = default
    }
}
