package fieldml.evaluator.datastore

import fieldml.domain.EnsembleDomain

class SemidenseDataDescription( val denseIndexes : Array[EnsembleDomain], val sparseIndexes : Array[EnsembleDomain] )
    extends DataDescription
{
    def indexDomains : Array[EnsembleDomain] = Array.concat( denseIndexes, sparseIndexes )
}
