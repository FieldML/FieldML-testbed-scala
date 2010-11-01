package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.valueType.ValueType
import fieldml.FieldmlObject

class ReferenceEvaluator( name : String, valueDomain : ValueType, val refEvaluator : Evaluator ) 
    extends Evaluator( name, valueDomain )
{
    val aliases = Map[ ValueType, FieldmlObject ]()
    

    def variables = refEvaluator.variables

    
    def alias( alias : Tuple2[ ValueType, FieldmlObject ] )
    {
        aliases( alias._1 ) = alias._2
    }
}
