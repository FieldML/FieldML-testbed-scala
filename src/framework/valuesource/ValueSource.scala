package framework.valuesource

import fieldml.evaluator.Evaluator

import framework.value.Value
import framework.EvaluationState

trait ValueSource
    extends Evaluator
{
    def evaluate( state : EvaluationState ) : Option[Value]
    
    
    def evaluateOrElse( state : EvaluationState, value : Value ) : Option[Value] = evaluate( state ) match{ case s : Some[Value] => s; case None => Some( value ) }
}
