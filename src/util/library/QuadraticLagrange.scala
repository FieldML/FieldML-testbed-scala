package util.library

class QuadraticLagrange( dimensions : Int )
    extends HomogeneousTensorBasis( dimensions )
{
    def basisFunction( xi : Double ) : Array[Double] =
    {
        Array( 2 * ( xi - 1 ) * ( xi - 0.5 ), 4 * ( xi ) * ( 1 - xi ), 2 * ( xi ) * ( xi - 0.5 ) )
    }
}
