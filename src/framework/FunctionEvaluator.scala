package framework

import fieldml.evaluator._
import fieldml.domain._

import value.ContinuousValue

class FunctionEvaluator( name : String, val function : ( Array[Double], Array[Double] ) => Array[Double], val domain1 : ContinuousDomain, val domain2 : ContinuousDomain, valueDomain : ContinuousDomain )
    extends Evaluator( name, valueDomain )
{
    def evaluate( state : EvaluationState ) : ContinuousValue =
    {
        val arg1 = state.get( domain1 ).get.value
        val arg2 = state.get( domain2 ).get.value

        return new ContinuousValue( function( arg1, arg2 ) )
    }
}
