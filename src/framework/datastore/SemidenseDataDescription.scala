package framework.datastore

import fieldml.evaluator.Evaluator

class SemidenseDataDescription( val denseIndexes : Array[Evaluator], val sparseIndexes : Array[Evaluator] )
    extends DataDescription
{
    override def indexEvaluators : Array[Evaluator] = Array.concat( denseIndexes, sparseIndexes )
}
