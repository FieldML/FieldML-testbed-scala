package fieldml

import framework.datastore.DataSource

class DataObject( name : String, val dataSource : DataSource, val entryCount : Int, val entryLength : Int, val entryHead : Int, val entryTail : Int )
    extends FieldmlObject( name )
{
}
