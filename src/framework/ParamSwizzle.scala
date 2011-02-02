package framework

import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

class ParamSwizzle( name : String, valueType : ValueType, val source : Evaluator, val swizzle : Array[Int] )
    extends Evaluator( name, valueType )
{
    def variables = source.variables
}
