package fieldml.evaluator

import scala.collection.mutable.Map 

import util.DefaultingHashMap

import fieldml.valueType.EnsembleType
import fieldml.valueType.ValueType
import fieldml.FieldmlObject

import framework.value.EnsembleValue

abstract class PiecewiseEvaluator( name : String, valueType : ValueType, val index : Evaluator )
    extends Evaluator( name, valueType )
{
    val delegations = new DefaultingHashMap[Int, Evaluator]()
    
    val binds = Map[ AbstractEvaluator, Evaluator ]()
    
    def variables = delegations.values.flatMap( _.variables )

    def bind( alias : Tuple2[ AbstractEvaluator, Evaluator ] )
    {
        binds( alias._1 ) = alias._2
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
