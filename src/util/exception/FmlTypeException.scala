package util.exception

import fieldml._

import fieldml.jni.FieldmlHandleType

class FmlTypeException( message : String )
    extends FmlException( message )
{
    def this( name : String, actualType : FieldmlHandleType, expectedType : FieldmlHandleType )
    {
        this( FmlTypeException.message( name, actualType, expectedType ) )
    }
    
    
    def this( obj : FieldmlObject, expectedType : FieldmlHandleType ) =
    {
        this( FmlTypeException.message( obj, expectedType ) )
    }
}


object FmlTypeException
{
    def message( name : String, actualType : FieldmlHandleType, expectedType : FieldmlHandleType ) : String =
    {
        return "Incorrect type for " + name + ": expected " + expectedType + ", got " + actualType
    }

    def message( obj : FieldmlObject, expectedType : FieldmlHandleType ) : String =
    {
        return "Incorrect type for " + obj.name + ": expected " + expectedType + ", got " + obj
    }
}
