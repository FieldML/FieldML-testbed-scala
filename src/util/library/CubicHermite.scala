package util.library

class CubicHermite( dimensions : Int )
    extends HomogeneousTensorBasis( dimensions )
{
    def basisFunction( xi : Double ) : Array[Double] =
    {
        Array( 
            1 - 3 * xi * xi + 2 * xi * xi * xi, // psi01
            xi * ( xi - 1 ) * ( xi - 1 ), // psi11
            xi * xi * ( 3 - 2 * xi ), //psi02
            xi * xi * ( xi - 1 ) //psi12
            );
    }
}
