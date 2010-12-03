package fieldml

import scala.collection.mutable.BitSet

import fieldml.valueType.ValueType

class ElementSet private( name : String, val ensemble : ValueType, private val set : BitSet )
    extends FieldmlObject( name )
{
    def this( name : String, ensemble : ValueType, elements : Iterable[Int] )
    {
        this( name, ensemble, BitSet() ++ elements )
    }

    
    def this( name : String, ensemble : ValueType, elements : Int* )
    {
        this( name, ensemble, BitSet( elements:_* ) )
    }
    
    
    def apply( e : Int ) = set.contains( e )
    
    
    def size = set.size
}
