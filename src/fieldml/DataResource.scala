package fieldml
import scala.collection.mutable.ArrayBuffer

class DataResource( name : String )
    extends FieldmlObject( name )
{
    val sources = ArrayBuffer[DataSource]()
}
