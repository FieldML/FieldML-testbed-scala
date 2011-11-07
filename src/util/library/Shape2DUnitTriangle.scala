package util.library

class Shape2DUnitTriangle
{
    def evaluate( xi : Array[Double] ) : Boolean =
    {
        return ( xi( 0 ) >= 0 ) && ( xi( 1 ) >= 0 ) && ( xi( 0 ) + xi( 1 ) <= 1 )
    }
}
