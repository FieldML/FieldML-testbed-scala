package framework

import fieldml.evaluator._
import fieldml.valueType._

import framework.valuesource._

import value.EnsembleValue

class EnsembleFunctionEvaluator( name : String, val function : ( Array[Double] ) => Int, val var1 : Evaluator, valueType : EnsembleType )
    extends Evaluator( name, valueType )
{
    private val _variables = var1.variables.toSeq.distinct
    
    def variables = _variables

    
    def evaluate( state : EvaluationState ) : Option[EnsembleValue] =
    {
        for( arg1 <- var1.evaluate( state ); v = function( arg1.cValue ) )
            yield new EnsembleValue( valueType, v )
    }
}
