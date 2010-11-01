package framework.valuesource

import fieldml.FieldmlObject

import framework.value.Value
import framework.Context

import framework.EvaluationState

import scala.collection.mutable.Stack

abstract class ValueSource( val valueType : FieldmlObject )
{
    def getValue( state : EvaluationState ) : Option[Value]
}
