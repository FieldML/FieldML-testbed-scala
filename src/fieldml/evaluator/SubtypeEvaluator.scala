package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.valueType.ValueType
import fieldml.valueType.StructuredType
import fieldml.FieldmlObject

class SubtypeEvaluator private( val baseEvaluator : Evaluator, valueType : ValueType, val subname : String ) 
    extends Evaluator( baseEvaluator.name + "." + subname, valueType )
{
    def this( baseEvaluator : Evaluator, subname : String )
    {
        this( baseEvaluator, baseEvaluator.valueType.asInstanceOf[StructuredType].subtype( subname ), subname )
    }
    
    
    def variables = baseEvaluator.variables
}
