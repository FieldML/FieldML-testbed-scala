package util.exception

import fieldml._

class FmlInvalidObjectException( message : String )
    extends FmlException( message )
{
    def this( obj : FieldmlObject, purpose : String ) =
    {
        this( FmlInvalidObjectException.message( obj, purpose ) )
    }
}


object FmlInvalidObjectException
{
    private def message( obj : FieldmlObject, purpose : String ) : String =
    {
        return obj.name + " cannot be used to " + purpose
    }
}