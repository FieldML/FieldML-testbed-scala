package framework

import fieldml.evaluator._
import fieldml.valueType._

import framework.valuesource._

import value.BooleanValue

class BooleanFunctionEvaluator( name : String, val function : ( Array[Double] ) => Boolean, val var1 : Evaluator, valueType : BooleanType )
    extends Evaluator( name, valueType )
{
    private val _variables = ( var1.variables ).toSeq.distinct
    
    def variables = _variables

    
    def evaluate( state : EvaluationState ) : Option[BooleanValue] =
    {
        for( arg1 <- var1.evaluate( state ); v = function( arg1.cValue ) )
            yield new BooleanValue( valueType, v )
    }
}
