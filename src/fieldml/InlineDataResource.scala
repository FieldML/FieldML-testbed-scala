package fieldml

class InlineDataResource( name : String, var data : String )
    extends DataResource( name )
{
    def this( name : String ) =
    {
        this( name, "" )
    }
}
