package framework

import fieldml.evaluator.ParameterEvaluator

package object datastore
{
    implicit def parameterDatastoreDescription( evaluator : ParameterEvaluator ) = evaluator.dataStore.description
}
