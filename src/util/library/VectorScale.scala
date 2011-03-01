package util.library

class VectorScale( dimensions : Int )
{
    def evaluate( v1 : Array[Double], v2 : Array[Double] ) : Array[Double] =
    {
        var a0 = Array.fill( dimensions )( 1.0 )
        
        for( d <- 0 until dimensions )
        {
            a0( d ) = v1( d ) * v2( d )
        }
        
        return a0
    }
}
