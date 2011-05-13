package fieldml

class TextFileDataResource( name : String, var href : String )
    extends DataResource( name )
{
    def this( name : String )
    {
        this( name, "" )
    }
}
