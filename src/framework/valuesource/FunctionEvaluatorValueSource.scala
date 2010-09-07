package framework.valuesource

import framework.value.Value
import framework.Context
import framework.FunctionEvaluator
import framework.EvaluationState

class FunctionEvaluatorValueSource( private val evaluator : FunctionEvaluator )
    extends EvaluatorValueSource( evaluator )
{
    override def getValue( state : EvaluationState ) : Option[Value] =
    {
        return Some( evaluator.evaluate( state ) )
    }
}
