package fieldml.valueType

import bounds._
import fieldml.evaluator.Evaluator

import util.DefaultingHashMap

class MeshType( name : String, elementBounds : EnsembleBounds, xiComponents : EnsembleType )
    extends StructuredType( name,
        Tuple2( "element", new EnsembleType( name + ".element", elementBounds, false ) ),
        Tuple2( "xi", new ContinuousType( name + ".xi", xiComponents ) )
        )
{
    val shapes = new DefaultingHashMap[Int, String]()
    
    val elementType = subtype( "element" ).get.asInstanceOf[EnsembleType]
    
    val xiType = subtype( "xi" ).get.asInstanceOf[ContinuousType]
}
