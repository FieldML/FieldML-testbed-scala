package util.library

class ShapeUnitLinear( val dimensions : Int )
{
    def evaluate( xi : Array[Double] ) : Boolean =
    {
        !xi.slice( 0, dimensions ).exists( x => x < 0 && x > 1 )
    }
}
