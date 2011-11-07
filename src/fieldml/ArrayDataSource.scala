package fieldml

class ArrayDataSource( name : String, val dataResource : DataResource, val location : String, val rank : Int )
    extends DataSource( name )
{
    var rawSizes = new Array[Int]( rank )
    
    var sizes = new Array[Int]( rank )

    var offsets = new Array[Int]( rank )
}
