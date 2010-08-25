package util.exception

import fieldml._

class FmlObjectCollisionException( message : String )
    extends FmlException( message )
{
    def this( object1 : FieldmlObject, object2 : FieldmlObject ) =
    {
        this( FmlObjectCollisionException.generateMessage( object1, object2 ) )
    }
}


object FmlObjectCollisionException
{
    def generateMessage( object1 : FieldmlObject, object2 : FieldmlObject ) : String =
    {
        return "Object collision betweem " + object1.name + " and " + object2.name
    }
}
