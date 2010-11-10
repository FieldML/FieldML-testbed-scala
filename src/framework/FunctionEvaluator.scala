package framework

import fieldml.evaluator._
import fieldml.valueType._

import framework.valuesource._

import value.ContinuousValue

class FunctionEvaluator( name : String, val function : ( Array[Double], Array[Double] ) => Array[Double], val var1 : AbstractEvaluator, val var2 : AbstractEvaluator, valueType : ContinuousType )
    extends Evaluator( name, valueType )
{
    val variables = Seq[AbstractEvaluator]( var1, var2 )

    
    def evaluate( state : EvaluationState ) : Option[ContinuousValue] =
    {
        for( arg1 <- var1.evaluate( state ); arg2 <- var2.evaluate( state ); v = function( arg1.cValue, arg2.cValue ) )
            yield new ContinuousValue( valueType, v )
    }
}
