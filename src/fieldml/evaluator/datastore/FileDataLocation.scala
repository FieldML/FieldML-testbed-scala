package fieldml.evaluator.datastore

class FileDataLocation( val filename : String, val offset : Int, val dataType : DataType.Value )
    extends DataLocation
{
}
