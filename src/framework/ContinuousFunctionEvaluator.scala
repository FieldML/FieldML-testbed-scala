package framework

import fieldml.evaluator._
import fieldml.valueType._

import framework.valuesource._

import value.ContinuousValue

class ContinuousFunctionEvaluator( name : String, val function : ( Array[Double], Array[Double] ) => Array[Double], val var1 : Evaluator, val var2 : Evaluator, valueType : ContinuousType )
    extends Evaluator( name, valueType )
{
    private val _variables = ( var1.variables ++ var2.variables ).toSeq.distinct
    
    def variables = _variables

    
    def evaluate( state : EvaluationState ) : Option[ContinuousValue] =
    {
        for( arg1 <- var1.evaluate( state ); arg2 <- var2.evaluate( state ); v = function( arg1.cValue, arg2.cValue ) )
            yield new ContinuousValue( valueType, v )
    }
}
