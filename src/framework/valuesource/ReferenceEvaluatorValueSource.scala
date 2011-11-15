package framework.valuesource

import fieldml.evaluator.ReferenceEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import framework.value.Value
import framework.Context
import framework.EvaluationState

class ReferenceEvaluatorValueSource( name : String, refEvaluator : Evaluator, valueType : ValueType ) 
    extends ReferenceEvaluator( name, refEvaluator )
    with GenericValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
      evaluateForType( state, valueType )
    }

    override def evaluateForType( state : EvaluationState,
                                  wantedType : ValueType ) =
    {
        state.pushAndApply( name, binds.toSeq )
        
        val v = refEvaluator match {
          case genValue : GenericValueSource =>
            genValue.evaluateForType( state, wantedType )
          case _ =>
            refEvaluator.evaluate( state )
        }
        
        state.pop()
        
        v
    }
}
