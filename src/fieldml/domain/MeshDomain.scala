package fieldml.domain

import bounds._
import fieldml.evaluator.Evaluator

import util.DefaultingHashMap

class MeshDomain( name : String, val elementBounds : EnsembleBounds, xiComponents : EnsembleDomain )
    extends Domain( name )
{
    val elementDomain = new EnsembleDomain( name + ".element", elementBounds, false )
    val xiDomain = new ContinuousDomain( name + ".xi", xiComponents )

    val shapes = new DefaultingHashMap[Int, String]()
    
    val connectivity = Map[Evaluator, Domain]()
}