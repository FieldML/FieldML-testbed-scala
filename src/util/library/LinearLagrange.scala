package util.library

class LinearLagrange( val dimensions : Int )
{
    private def linearLagrange( xi : Double ) : Array[Double] =
    {
        return Array( 1 - xi, xi )
    }
    
    
    private def outerProduct( a1 : Array[Double], a2 : Array[Double] ) : Array[Double] =
    {
        val a3 = Array.fill[Double]( a1.size * a2.size)( 1.0 )
        
        for(
            i <- 0 until a1.size ;
            j <- 0 until a2.size )
        {
            a3( ( j * a1.size ) + i ) = a1( i ) * a2( j )
        }
        
        return a3
    }
    
    
    private def dotProduct( a1 : Array[Double], a2 : Array[Double] ) : Array[Double] =
    {
        val a3 = Array[Double]( 1 )

        a3( 0 ) = 0
        for( i <- 0 until a1.size )
        {
            a3( 0 ) += a1( i ) * a2( i )
        }
        
        return a3
    }
    
    
    def evaluate( xi : Array[Double], params : Array[Double] ) : Array[Double] =
    {
        var a0 = Array.fill( 1 )( 1.0 )
        
        for( d <- 0 until dimensions )
        {
            a0 = outerProduct( a0, linearLagrange( xi( d ) ) )
        }
        
        a0 = dotProduct( a0, params )
        
        return a0
    }
}