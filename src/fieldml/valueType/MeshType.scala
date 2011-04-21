package fieldml.valueType

import fieldml.evaluator.Evaluator

import util.DefaultingHashMap

class MeshType( name : String, xiComponents : EnsembleType )
    extends StructuredType( name,
        Tuple2( "elements", new EnsembleType( name + ".elements", false ) ),
        Tuple2( "xi", new ContinuousType( name + ".xi", xiComponents ) )
        )
{
    val shapes = new DefaultingHashMap[Int, String]()
    
    val elementType = subtype( "elements" ).asInstanceOf[EnsembleType]
    
    val xiType = subtype( "xi" ).asInstanceOf[ContinuousType]
}
