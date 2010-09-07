package fieldml.evaluator

import scala.collection.mutable.Map

import fieldml.domain.Domain
import fieldml.FieldmlObject

class ReferenceEvaluator( name : String, valueDomain : Domain, val refEvaluator : Evaluator ) 
    extends Evaluator( name, valueDomain )
{
    val aliases = Map[ Domain, FieldmlObject ]()
    

    def alias( alias : Tuple2[ Domain, FieldmlObject ] )
    {
        aliases( alias._1 ) = alias._2
    }
}
