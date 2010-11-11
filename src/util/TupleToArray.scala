package util

object TupleToArray
{
    def tupleToIntArray( tuple : Product ) : Option[Array[Int]] =
    {
        if( !tuple.productIterator.forall( _.isInstanceOf[Int] ) )
        {
            None
        }
        else
        {
            Some( tuple.productIterator.map( _.asInstanceOf[Int] ).toArray[Int] )
        }
    }
    
    
    def tupleIsInts( tuple : Product ) : Boolean = tuple.productIterator.forall( _.isInstanceOf[Int] )


    def tupleToDoubleArray( tuple : Product ) : Option[Array[Double]] =
    {
        if( !tuple.productIterator.forall( _.isInstanceOf[Double] ) )
        {
            None
        }
        else
        {
            Some( tuple.productIterator.map( _.asInstanceOf[Double] ).toArray[Double] )
        }
    }
    
    
    def tupleIsDoubles( tuple : Product ) : Boolean = tuple.productIterator.forall( _.isInstanceOf[Double] )
}