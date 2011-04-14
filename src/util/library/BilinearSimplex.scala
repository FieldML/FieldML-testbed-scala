package util.library

class BilinearSimplex()
{
    private def basisFunction( xi : Array[Double] ) : Array[Double] =
    {
        Array( xi(0), xi(1), 1 - ( xi(0) + xi(1) ) )
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
        var a0 = basisFunction( xi )
        
        a0 = dotProduct( a0, params )
        
        return a0
    }
}