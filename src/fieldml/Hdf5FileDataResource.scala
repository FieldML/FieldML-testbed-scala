package fieldml

class Hdf5FileDataResource( name : String, var href : String )
    extends DataResource( name )
{
    def this( name : String )
    {
        this( name, "" )
    }
}
