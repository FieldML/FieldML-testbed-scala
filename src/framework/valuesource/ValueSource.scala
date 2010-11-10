package framework.valuesource

import fieldml.evaluator.Evaluator

import framework.value.Value
import framework.EvaluationState

trait ValueSource
    extends Evaluator
{
    def evaluate( state : EvaluationState ) : Option[Value]
}
