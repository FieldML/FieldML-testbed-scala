package util.library

class GridRefinementElement( val dimensions : Array[Int] )
{
    def evaluate( xi : Array[Double] ) : Int =
    {
        val gridCoords = xi.zip( dimensions ).map( x => if( x._1 >= 1.0 ) x._2 - 1 else if( x._1 < 0 ) 0 else ( x._1 * x._2 ).toInt )
        dimensions.zip( gridCoords ).foldRight( 0 )( ( y, elementNumber ) => ( elementNumber * y._1 ) + y._2 ) + 1
    }
}
