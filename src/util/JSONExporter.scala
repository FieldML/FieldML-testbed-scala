package util

import framework.region._

import framework.value.ContinuousValue
import framework.value.Value

import fieldml.evaluator.AbstractEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.MeshType

object JSONExporter
{
    private def addDoubleTri( string : StringBuilder, x : Double, y : Double, z : Double )
    {
        if( string.length > 0 )
        {
            string.append( ", " )
            string.append( "\n" )
        }
        string.append( x )

        string.append( ", " )
        string.append( y )
        
        string.append( ", " )
        string.append( z )
    }

    
    private def addIntTri( string : StringBuilder, x : Int, y : Int, z : Int )
    {
        if( string.length > 0 )
        {
            string.append( ", " )
            string.append( "\n" )
        }
        string.append( x )

        string.append( ", " )
        string.append( y )
        
        string.append( ", " )
        string.append( z )
    }

    
    private def buildJson( pairs : Pair[String, String]* ) : String =
    {
        var json = new StringBuilder()
        
        json.append( "{\n" )
        
        var firstEntry = true
        for( p <- pairs )
        {
            if( !firstEntry )
            {
                json.append( ",\n" )
            }
            json.append( "    \"" + p._1 + "\" : [\n" + p._2 + "\n    ]" )
            firstEntry = false
        }

        json.append( "\n}\n" )
        
        json.toString
    }

    
    def export2DTrisFromFieldML( region : Region, meshName : String, evaluatorName : String ) : String =
    {
        val meshVariable : AbstractEvaluator = region.getObject( meshName )
        val meshType = meshVariable.valueType.asInstanceOf[MeshType]
        val meshEvaluator : Evaluator = region.getObject( evaluatorName )
        val elementCount = meshType.elementType.elementCount

        val coords = new StringBuilder()
        val verts = new StringBuilder()
        
        for( elementNumber <- 1 to elementCount )
        {
            for( i <- 0 to 1 )
            {
                for( j <- 0 to 1 )
                {
                    val xi1 : Double = i * 1.0
                    val xi2 : Double = j * 1.0
                    if( xi1 + xi2 <= 1.0 )
                    {
                        region.bind( meshVariable, elementNumber, xi1, xi2 )
                        
                        val value = region.evaluate( meshEvaluator )
                        addDoubleTri( coords, value.get.cValue(0), value.get.cValue(1), value.get.cValue(2) )
                    }
                }
            }
            
            addIntTri( verts, ( elementNumber - 1 ) * 3 + 0, ( elementNumber - 1 ) * 3 + 1, ( elementNumber - 1 ) * 3 + 2 )
        }

        buildJson( "vertexPositions" -> coords.toString, "indices" -> verts.toString )
    }


    def export2DFromFieldML( region : Region, discretization : Int, meshName : String, evaluatorName : String ) : String =
    {
        val meshVariable : AbstractEvaluator = region.getObject( meshName )
        val meshType = meshVariable.valueType.asInstanceOf[MeshType]
        val meshEvaluator : Evaluator = region.getObject( evaluatorName )
        val elementCount = meshType.elementType.elementCount

        val coords = new StringBuilder()
        val verts = new StringBuilder()
        
        for( elementNumber <- 1 to elementCount )
        {
            for( i <- 0 to 1 )
            {
                for( j <- 0 to 1 )
                {
                    val xi1 : Double = i * 1.0
                    val xi2 : Double = j * 1.0

                    region.bind( meshVariable, elementNumber, xi1, xi2 )
                        
                    val value = region.evaluate( meshEvaluator )
                    addDoubleTri( coords, value.get.cValue(0), value.get.cValue(1), value.get.cValue(2) )
                }
            }

            addIntTri( verts, ( elementNumber - 1 ) * 4 + 0, ( elementNumber - 1 ) * 4 + 1, ( elementNumber - 1 ) * 4 + 2 )
            addIntTri( verts, ( elementNumber - 1 ) * 4 + 1, ( elementNumber - 1 ) * 4 + 3, ( elementNumber - 1 ) * 4 + 2 )
        }

        buildJson( "vertexPositions" -> coords.toString, "indices" -> verts.toString )
    }
}