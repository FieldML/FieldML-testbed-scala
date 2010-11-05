package fieldml.evaluator

import fieldml.valueType.ValueType

abstract class AbstractEvaluator( name : String, valueType : ValueType, explicitVariables : AbstractEvaluator* )
    extends Evaluator( name, valueType )
{
    private val _variables = ( ( explicitVariables :+ this ) ++ explicitVariables.flatMap( _.variables ) ).distinct
    
    def variables = _variables

    val binds = Map[ AbstractEvaluator, Evaluator ]()
    
    def bind( _bind : Tuple2[ AbstractEvaluator, Evaluator ] )
    {
        binds( _bind._1 ) = _bind._2
    }
}
