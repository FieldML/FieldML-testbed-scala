package framework.valuesource

import fieldml.valueType.ValueType
import framework.value.Value
import framework.EvaluationState

trait GenericValueSource
    extends ValueSource
{
    def evaluateForType( state : EvaluationState, wantedType : ValueType ) :
      Option[Value]
}
