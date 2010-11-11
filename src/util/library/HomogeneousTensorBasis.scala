package util.library

abstract class HomogeneousTensorBasis( val dimensions : Int )
{
    def basisFunction( xi : Double ) : Array[Double]
    
    
    private def outerProduct( a1 : Array[Double], a2 : Array[Double] ) : Array[Double] =
    {
        ( for(
            j <- 0 until a2.size;
            i <- 0 until a1.size )
            yield a1( i ) * a2( j ) ).toArray
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
            a0 = outerProduct( a0, basisFunction( xi( d ) ) )
        }
        
        a0 = dotProduct( a0, params )
        
        return a0
    }
}