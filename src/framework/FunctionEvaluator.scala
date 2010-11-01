package framework

import fieldml.evaluator._
import fieldml.valueType._

import value.ContinuousValue

class FunctionEvaluator( name : String, val function : ( Array[Double], Array[Double] ) => Array[Double], val var1 : AbstractEvaluator, val var2 : AbstractEvaluator, valueType : ContinuousType )
    extends Evaluator( name, valueType )
{
    val variables = Seq[AbstractEvaluator]( var1, var2 )

    
    def evaluate( state : EvaluationState ) : ContinuousValue =
    {
        val arg1 = state.get( var1 ).get.cValue
        val arg2 = state.get( var2 ).get.cValue

        return new ContinuousValue( valueType, function( arg1, arg2 ) )
    }
}
