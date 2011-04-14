package framework.datastore

class InlineDataSource( var data : String )
    extends DataSource
{
    def this() =
    {
        this( "" )
    }
}
