package fieldml.evaluator.datastore

class InlineDataLocation( var data : String )
    extends DataLocation
{
    def this() =
    {
        this( "" )
    }
}