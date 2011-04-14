package fieldml.evaluator

import scala.collection.mutable.Map 

import util.DefaultingHashMap

import fieldml.valueType.EnsembleType
import fieldml.valueType.ContinuousType
import fieldml.FieldmlObject

abstract class AggregateEvaluator( name : String, valueType : ContinuousType )
    extends Evaluator( name, valueType )
{
    val componentEvaluators = new DefaultingHashMap[Int, Evaluator]()
    
    val binds = Map[ Evaluator, Evaluator ]()
    
    //NOTE Currently, continuous types can have no more than one component index
    val indexBinds = Map[ Int, Evaluator ]()
    
    def variables = componentEvaluators.values.flatMap( _.variables )

    def bind( _bind : Tuple2[ Evaluator, Evaluator ] )
    {
        binds( _bind._1 ) = _bind._2
    }
    
    
    def bind_index( _bind : Tuple2[ Int, Evaluator ] )
    {
        indexBinds( _bind._1 ) = _bind._2
    }

    
    def map( pair : Tuple2[ Int, Evaluator] )
    {
        componentEvaluators( pair._1 ) = pair._2
    }
    
    
    def setDefault( default : Evaluator )
    {
        componentEvaluators.default = default
    }
}
