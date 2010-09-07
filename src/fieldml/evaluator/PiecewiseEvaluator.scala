package fieldml.evaluator

import util.DefaultingHashMap

import fieldml.domain.EnsembleDomain
import fieldml.domain.Domain
import fieldml.FieldmlObject

class PiecewiseEvaluator( name : String, valueDomain : Domain, val index : EnsembleDomain )
    extends Evaluator( name, valueDomain )
{
    val delegations = new DefaultingHashMap[Int, FieldmlObject]()
    
    val aliases = Map[ Domain, FieldmlObject ]()
    

    def alias( alias : Tuple2[ Domain, FieldmlObject ] )
    {
        aliases( alias._1 ) = alias._2
    }


    def map( pair : Tuple2[ Int, FieldmlObject ] )
    {
        delegations( pair._1 ) = pair._2
    }
    
    
    def setDefault( default : FieldmlObject )
    {
        delegations.default = default
    }
}
