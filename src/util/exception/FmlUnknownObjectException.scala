package util.exception

class FmlUnknownObjectException( message : String )
    extends FmlException( message )
{
    def this( objectHandle : Int ) =
    {
        this( FmlUnknownObjectException.message( objectHandle ) )
    }
    
    
    def this( objectName : String, regionName : String ) =
    {
        this( FmlUnknownObjectException.message( objectName, regionName ) )
    }
}


object FmlUnknownObjectException
{
    def message( objectHandle : Int ) : String =
    {
        return "Object handle " + objectHandle + " is invalid"    
    }
    
    
    def message( objectName : String, regionName : String ) : String =
    {
        return "Object named " + objectName + " cannot be found in region " + regionName    
    }
}
