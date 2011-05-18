package fieldml.valueType

import fieldml.evaluator.Evaluator

import util.DefaultingHashMap

class MeshType( name : String, xiComponents : EnsembleType )
    extends StructuredType( name,
        Tuple2( "element", new EnsembleType( name + ".element", false ) ),
        Tuple2( "xi", new ContinuousType( name + ".xi", xiComponents ) )
        )
{
    def this( name : String, elementCount : Int, dimensions : Int )
    {
        this( name, new EnsembleType( name + ".xi.component", true ) )
        
        elementType.elementSet.add( 1, elementCount, 1 )
        xiType.componentType.elementSet.add( 1, dimensions, 1 )
    }
    
    
    val shapes = new DefaultingHashMap[Int, String]()
    
    val elementType = subtype( "element" ).asInstanceOf[EnsembleType]
    
    val xiType = subtype( "xi" ).asInstanceOf[ContinuousType]
}
