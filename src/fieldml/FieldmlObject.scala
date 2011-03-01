package fieldml

import scala.collection.mutable.Map

class FieldmlObject( val name : String )
{
    var isLocal : Boolean = true
    
    val markup : Map[String, String] = Map[String, String]()
}
