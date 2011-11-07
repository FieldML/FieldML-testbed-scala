package fieldml.valueType

import fieldml.evaluator.Evaluator

import util.DefaultingHashMap

class MeshType( name : String, xiComponents : EnsembleType, val elementName : String, val xiName : String )
    extends StructuredType( name,
        Tuple2( elementName, new EnsembleType( name + "." + elementName, false ) ),
        Tuple2( xiName, new ContinuousType( name + "." + xiName, xiComponents ) )
        )
{
    def this( name : String, elementCount : Int, dimensions : Int )
    {
        this( name, new EnsembleType( name + ".xi.component", true ), "element", "xi" )
        
        elementType.elementSet.add( 1, elementCount, 1 )
        xiType.componentType.elementSet.add( 1, dimensions, 1 )
    }
    
    
    var shapes : Evaluator = null
    
    val elementType = subtype( elementName ).asInstanceOf[EnsembleType]
    
    val xiType = subtype( xiName ).asInstanceOf[ContinuousType]
}
