package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.valueType.ValueType
import fieldml.valueType.EnsembleType

import framework.datastore._
import framework.value._

class ParameterEvaluator( name : String, valueType : ValueType, var dataStore : DataStore )
    extends Evaluator( name, valueType )
{
    def variables = dataStore.description.indexEvaluators.flatMap( _.variables ).distinct
}
