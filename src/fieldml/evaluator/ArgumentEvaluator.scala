package fieldml.evaluator

import fieldml.valueType.ValueType

abstract class ArgumentEvaluator( name : String, valueType : ValueType, explicitVariables : ArgumentEvaluator* )
    extends Evaluator( name, valueType )
{
    private val _variables = ( ( explicitVariables :+ this ) ++ explicitVariables.flatMap( _.variables ) ).distinct
    
    def variables = _variables
}
