package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.valueType.ValueType
import fieldml.FieldmlObject

class ConstantEvaluator( name : String, val valueString : String, valueType : ValueType ) 
    extends Evaluator( name, valueType )
{
    def variables = None
}
