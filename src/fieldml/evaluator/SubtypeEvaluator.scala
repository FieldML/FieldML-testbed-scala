package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.valueType.ValueType
import fieldml.FieldmlObject

class SubtypeEvaluator( val baseEvaluator : Evaluator, valueType : ValueType, val subname : String ) 
    extends Evaluator( baseEvaluator.name + "." + subname, valueType )
{
    def variables = baseEvaluator.variables
}
