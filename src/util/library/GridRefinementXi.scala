package util.library

class GridRefinementXi( val dimensions : Array[Int] )
{
    def evaluate( xi : Array[Double], dummy : Array[Double] ) : Array[Double] =
    {
        xi.zip( dimensions ).map( x => if( x._1 >= 1.0 ) 1.0 else if( x._1 < 0 ) 0 else ( x._1 * x._2 ) % 1 )
    }
}
