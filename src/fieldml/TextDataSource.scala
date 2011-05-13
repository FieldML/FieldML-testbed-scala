package fieldml

class TextDataSource( name : String, val dataResource : DataResource, val firstLine : Int, val entryCount : Int, val entryLength : Int, val entryHead : Int, val entryTail : Int )
    extends DataSource( name )
{

}
