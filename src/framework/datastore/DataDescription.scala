package framework.datastore

import fieldml.evaluator.Evaluator

abstract class DataDescription
{
    def indexEvaluators : Array[Evaluator]
}
