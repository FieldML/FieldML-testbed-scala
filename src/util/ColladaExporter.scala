package util

import framework.region._

import framework.value.ContinuousValue
import framework.value.Value

import fieldml.evaluator.AbstractEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.MeshType

/**
 * Very very simplistic FieldML-java to Collada converter.
 */
object ColladaExporter
{
    private val rawXml = """<?xml version="1.0" encoding="utf-8"?>
<COLLADA version="1.4.0" xmlns="http://www.collada.org/2005/11/COLLADASchema">
    <library_cameras>
        <camera id="Camera-Camera" name="Camera-Camera">
            <optics>
                <technique_common>
                    <perspective>
                        <yfov>49.13434</yfov>
                        <znear>0.1</znear>
                        <zfar>100.0</zfar>
                    </perspective>
                </technique_common>
            </optics>
        </camera>
    </library_cameras>
    <library_effects>
        <effect id="Material-fx" name="Material-fx">
            <profile_COMMON>
                <technique sid="blender">
                    <phong>
                        <emission>
                            <color>0.00000 0.00000 0.00000 1</color>
                        </emission>
                        <ambient>
                            <color>0.40000 0.40000 0.40000 1</color>
                        </ambient>
                        <diffuse>
                            <color>0.80000 0.80000 0.80000 1</color>
                        </diffuse>
                        <specular>
                            <color>0.50000 0.50000 0.50000 1</color>
                        </specular>
                        <shininess>
                            <float>12.5</float>
                        </shininess>
                        <reflective>
                            <color>1.00000 1.00000 1.00000 1</color>
                        </reflective>
                        <reflectivity>
                            <float>0.0</float>
                        </reflectivity>
                        <transparent>
                            <color>1 1 1 1</color>
                        </transparent>
                        <transparency>
                            <float>0.0</float>
                        </transparency>
                    </phong>
                </technique>
            </profile_COMMON>
        </effect>
    </library_effects>
    <library_lights>
        <light id="Spot" name="Spot">
            <technique_common>
                <point>
                    <color>1.00000 1.00000 1.00000</color>
                    <constant_attenuation>1.0</constant_attenuation>
                    <linear_attenuation>0.0</linear_attenuation>
                    <quadratic_attenuation>0.0</quadratic_attenuation>
                </point>
            </technique_common>
        </light>
    </library_lights>
    <library_materials>
        <material id="Material" name="Material">
            <instance_effect url="#Material-fx"/>
        </material>
    </library_materials>
    <library_geometries>
        <geometry id="Cube_003" name="Cube_003">
            <mesh>
                <source id="Cube_003-Position">
                    <float_array count="xyzArrayCount" id="Cube_003-Position-array">xyzArray</float_array>
                    <technique_common>
                        <accessor count="vertexCount" source="#Cube_003-Position-array" stride="3">
                            <param type="float" name="X"></param>
                            <param type="float" name="Y"></param>
                            <param type="float" name="Z"></param>
                        </accessor>
                    </technique_common>
                </source>
                <vertices id="Cube_003-Vertex">
                    <input semantic="POSITION" source="#Cube_003-Position"/>
                </vertices>
                <polygons count="polygonCount" material="Material">
                    <input offset="0" semantic="VERTEX" source="#Cube_003-Vertex"/>
polygonBlock

                </polygons>
            </mesh>
        </geometry>
    </library_geometries>
    <library_visual_scenes>
        <visual_scene id="Scene" name="Scene">
            <node layer="L1" id="Cube" name="Cube">
                <translate sid="translate">0.00000 0.00000 0.00000</translate>
                <rotate sid="rotateZ">0 0 1 0.00000</rotate>
                <rotate sid="rotateY">0 1 0 -0.00000</rotate>
                <rotate sid="rotateX">1 0 0 0.00000</rotate>
                <scale sid="scale">1.00000 1.00000 1.00000</scale>
                <instance_geometry url="#Cube_003">
                    <bind_material>
                        <technique_common>
                            <instance_material symbol="Material" target="#Material">
                                <bind_vertex_input input_semantic="TEXCOORD" input_set="1" semantic="CHANNEL1"/>
                            </instance_material>
                        </technique_common>
                    </bind_material>
                </instance_geometry>
            </node>
            <node layer="L1" id="Lamp" name="Lamp">
                <translate sid="translate">4.07625 1.00545 5.90386</translate>
                <rotate sid="rotateZ">0 0 1 106.93632</rotate>
                <rotate sid="rotateY">0 1 0 3.16371</rotate>
                <rotate sid="rotateX">1 0 0 37.26105</rotate>
                <scale sid="scale">1.00000 1.00000 1.00000</scale>
                <instance_light url="#Spot"/>
            </node>
            <node layer="L1" id="Camera" name="Camera">
                <translate sid="translate">7.48113 -6.50764 5.34367</translate>
                <rotate sid="rotateZ">0 0 1 46.69194</rotate>
                <rotate sid="rotateY">0 1 0 0.61977</rotate>
                <rotate sid="rotateX">1 0 0 63.55930</rotate>
                <scale sid="scale">1.00000 1.00000 1.00000</scale>
                <instance_camera url="#Camera-Camera"/>
            </node>
        </visual_scene>
    </library_visual_scenes>
    <library_physics_materials>
        <physics_material id="Cube-PhysicsMaterial" name="Cube-PhysicsMaterial">
            <technique_common>
                <dynamic_friction>0.5</dynamic_friction>
                <restitution>0.0</restitution>
                <static_friction>0.5</static_friction>
            </technique_common>
        </physics_material>
    </library_physics_materials>
    <library_physics_models>
        <physics_model id="Scene-PhysicsModel" name="Scene-PhysicsModel">
            <rigid_body name="Cube-RigidBody" sid="Cube-RigidBody">
                <technique_common>
                    <dynamic>false</dynamic>
                    <mass>0</mass>
                    <instance_physics_material url="#Cube-PhysicsMaterial"/>
                    <shape>
                        <instance_geometry url="#Cube_003"/>
                    </shape>
                </technique_common>
            </rigid_body>
        </physics_model>
    </library_physics_models>
    <library_physics_scenes>
        <physics_scene id="Scene-Physics" name="Scene-Physics">
            <instance_physics_model url="#Scene-PhysicsModel">
                <instance_rigid_body body="Cube-RigidBody" target="#Cube"/>
            </instance_physics_model>
        </physics_scene>
    </library_physics_scenes>
    <scene>
        <instance_physics_scene url="#Scene-Physics"/>
        <instance_visual_scene url="#Scene"/>
    </scene>
</COLLADA>    		
    """


    private def fillInColladaTemplate( xyzArray : StringBuilder, polygonBlock : StringBuilder, polygonCount : Int,
        vertexCount : Int, xyzArrayCount : Int ) : String =
    {
        val fullCollada = new StringBuilder( rawXml )

        {
            val xyzArrayCountToken = "xyzArrayCount"
            searchAndReplaceOnce( fullCollada, xyzArrayCountToken, "" + xyzArrayCount )
        }

        {
            val xyzArrayToken = "xyzArray"
            searchAndReplaceOnce( fullCollada, xyzArrayToken, xyzArray.toString() )
        }

        {
            val vertexCountToken = "vertexCount"
            searchAndReplaceOnce( fullCollada, vertexCountToken, "" + vertexCount )
        }

        {
            val polygonCountToken = "polygonCount"
            searchAndReplaceOnce( fullCollada, polygonCountToken, "" + polygonCount )
        }

        {
            val polygonBlockToken = "polygonBlock"
            searchAndReplaceOnce( fullCollada, polygonBlockToken, polygonBlock.toString() )
        }

        val colladaString = fullCollada.toString()
        return colladaString
    }


    private def searchAndReplaceOnce( subjectText : StringBuilder, token : String, string : String ) : Unit =
    {
        val tokenStart = subjectText.indexOf( token )
        subjectText.replace( tokenStart, tokenStart + token.length, string )
    }


    private def appendTriple( value : Option[Value], xyzArray : StringBuilder ) : Unit =
    {
        value match
        {
            case c : Some[ContinuousValue] =>  xyzArray.append( "\n" ); xyzArray.append( " " + c.get.value(0) + " " + c.get.value(1) + " " + c.get.value(2) );
            case _ => xyzArray.append( "\n 0 0 0" )
        }
    }

    private def appendDouble( value : Option[Value], z : Double, xyzArray : StringBuilder ) : Unit =
    {
        value match
        {
            case c : Some[ContinuousValue] =>  xyzArray.append( "\n" ); xyzArray.append( " " + c.get.value(0) + " " + c.get.value(1) + " " + z );
            case _ => xyzArray.append( "\n 0 0 0" )
        }
    }

    private def appendDouble( value : Option[Value], zValue : Option[Value], xyzArray : StringBuilder ) : Unit =
    {
        value match
        {
            case c : Some[ContinuousValue] => zValue match
            {
                case d : Some[ContinuousValue] => xyzArray.append( "\n" ); xyzArray.append( " " + c.get.value(0) + " " + c.get.value(1) + " " + d.get.value(0) );
                case _ => xyzArray.append( "\n 0 0 0" )
            }
            case _ => xyzArray.append( "\n 0 0 0" )
        }
    }

    private def appendSingle( value : Option[Value], y : Double, z : Double, xyzArray : StringBuilder ) : Unit =
    {
        value match
        {
            case c : Some[ContinuousValue] =>  xyzArray.append( "\n" ); xyzArray.append( " " + c.get.value(0) + " " + y + " " + z );
            case _ => xyzArray.append( "\n 0 0 0" )
        }
    }

    def export3DFromFieldML( region : Region, discretisation : Int, meshName : String, evaluatorName : String ) : String =
    {
        val meshVariable : AbstractEvaluator = region.getObject( meshName )
        val meshType = meshVariable.valueType.asInstanceOf[MeshType]
        val meshEvaluator : Evaluator = region.getObject( evaluatorName )
        val elementCount = meshType.elementType.bounds.elementCount

        val xyzArray = new StringBuilder()
        val polygonBlock = new StringBuilder()
        for( elementNumber <- 1 to elementCount )
        {
            for( i <- 0 to discretisation )
            {
                for( j <- 0 to discretisation )
                {
                    val xi1 : Double = i * 1.0 / discretisation
                    val xi2 : Double = j * 1.0 / discretisation
                    
                    region.bind( meshVariable, elementNumber, xi1, xi2, 0 )
                    
                    val value = region.evaluate( meshEvaluator )
                    appendTriple( value, xyzArray )

                    region.bind( meshVariable, elementNumber, xi1, xi2, 1 )
                    
                    val value2 = region.evaluate( meshEvaluator )
                    appendTriple( value2, xyzArray )
                }
            }
            xyzArray.append( "\n" )

            val nodeOffsetOfElement = ( elementNumber - 1 ) * ( discretisation + 1 ) * ( discretisation + 1 )
            for( i <- 0 until discretisation )
            {
                for( j <- 0 until discretisation )
                {
                    val nodeAtLowerXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 0 )
                    val nodeAtLowerXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 1 )
                    val nodeAtUpperXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 1 )
                    val nodeAtUpperXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 0 )
                    polygonBlock.append( "<p>" )
                    polygonBlock.append( " " + (nodeAtLowerXi1LowerXi2*2 ))
                    polygonBlock.append( " " + (nodeAtLowerXi1UpperXi2*2 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1UpperXi2*2 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1LowerXi2*2 ))
                    polygonBlock.append( "</p>\n" )
                    polygonBlock.append( "<p>" )
                    polygonBlock.append( " " + (nodeAtLowerXi1LowerXi2*2 + 1 ))
                    polygonBlock.append( " " + (nodeAtLowerXi1UpperXi2*2 + 1 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1UpperXi2*2 + 1 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1LowerXi2*2 + 1))
                    polygonBlock.append( "</p>\n" )
                }
            }
        }

        val polygonCount = discretisation * discretisation * elementCount
        val vertexCount = ( discretisation + 1 ) * ( discretisation + 1 ) * elementCount
        val xyzArrayCount = vertexCount * 3

        val colladaString = fillInColladaTemplate( xyzArray, polygonBlock, polygonCount*2, vertexCount*2, xyzArrayCount*2 )

        return colladaString
    }


    def export2DFromFieldML( region : Region, discretisation : Int, meshName : String, evaluatorName : String ) : String =
    {
        val meshVariable : AbstractEvaluator = region.getObject( meshName )
        val meshType = meshVariable.valueType.asInstanceOf[MeshType]
        val meshEvaluator : Evaluator = region.getObject( evaluatorName )
        val elementCount = meshType.elementType.bounds.elementCount

        val xyzArray = new StringBuilder()
        val polygonBlock = new StringBuilder()
        for( elementNumber <- 1 to elementCount )
        {
            for( i <- 0 to discretisation )
            {
                for( j <- 0 to discretisation )
                {
                    val xi1 : Double = i * 1.0 / discretisation
                    val xi2 : Double = j * 1.0 / discretisation
                    
                    region.bind( meshVariable, elementNumber, xi1, xi2 )
                    
                    val value = region.evaluate( meshEvaluator )
                    
                    appendTriple( value, xyzArray )
                }
            }
            xyzArray.append( "\n" )

            val nodeOffsetOfElement = ( elementNumber - 1 ) * ( discretisation + 1 ) * ( discretisation + 1 )
            for( i <- 0 until discretisation )
            {
                for( j <- 0 until discretisation )
                {
                    val nodeAtLowerXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 0 )
                    val nodeAtLowerXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 1 )
                    val nodeAtUpperXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 1 )
                    val nodeAtUpperXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 0 )
                    polygonBlock.append( "<p>" )
                    polygonBlock.append( " " + (nodeAtLowerXi1LowerXi2 ))
                    polygonBlock.append( " " + (nodeAtLowerXi1UpperXi2 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1UpperXi2 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1LowerXi2 ))
                    polygonBlock.append( "</p>\n" )
                }
            }
        }

        val polygonCount = discretisation * discretisation * elementCount
        val vertexCount = ( discretisation + 1 ) * ( discretisation + 1 ) * elementCount
        val xyzArrayCount = vertexCount * 3

        val colladaString = fillInColladaTemplate( xyzArray, polygonBlock, polygonCount, vertexCount, xyzArrayCount )

        return colladaString
    }


    def export2DFromFieldML( region : Region, discretisation : Int, meshName : String, geometryName : String, valueName : String ) : String =
    {
        val meshVariable : AbstractEvaluator = region.getObject( meshName )
        val meshType = meshVariable.valueType.asInstanceOf[MeshType]
        val meshEvaluator : Evaluator = region.getObject( geometryName )
        val heightEvaluator : Evaluator = region.getObject( valueName )
        val elementCount = meshType.elementType.bounds.elementCount

        val xyzArray = new StringBuilder()
        val polygonBlock = new StringBuilder()
        for( elementNumber <- 1 to elementCount )
        {
            for( i <- 0 to discretisation )
            {
                for( j <- 0 to discretisation )
                {
                    val xi1 : Double = i * 1.0 / discretisation
                    val xi2 : Double = j * 1.0 / discretisation
                    
                    region.bind( meshVariable, elementNumber, xi1, xi2, 0 )
                    
                    val value = region.evaluate( meshEvaluator )
                    
                    val zValue = region.evaluate( heightEvaluator )
                    
                    appendDouble( value, zValue, xyzArray )
                }
            }
            xyzArray.append( "\n" )

            val nodeOffsetOfElement = ( elementNumber - 1 ) * ( discretisation + 1 ) * ( discretisation + 1 )
            for( i <- 0 until discretisation )
            {
                for( j <- 0 until discretisation )
                {
                    val nodeAtLowerXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 0 )
                    val nodeAtLowerXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 1 )
                    val nodeAtUpperXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 1 )
                    val nodeAtUpperXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 0 )
                    polygonBlock.append( "<p>" )
                    polygonBlock.append( " " + (nodeAtLowerXi1LowerXi2 ))
                    polygonBlock.append( " " + (nodeAtLowerXi1UpperXi2 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1UpperXi2 ))
                    polygonBlock.append( " " + (nodeAtUpperXi1LowerXi2 ))
                    polygonBlock.append( "</p>\n" )
                }
            }
        }

        val polygonCount = discretisation * discretisation * elementCount
        val vertexCount = ( discretisation + 1 ) * ( discretisation + 1 ) * elementCount
        val xyzArrayCount = vertexCount * 3

        val colladaString = fillInColladaTemplate( xyzArray, polygonBlock, polygonCount, vertexCount, xyzArrayCount )

        return colladaString
    }


    def exportFromFieldML( region : Region, discretisation : Int, evaluatorName : String, mesh1Name : String,
        mesh2Name : String ) : String =
    {
        val mesh1Variable : AbstractEvaluator = region.getObject( mesh1Name )
        val mesh2Variable : AbstractEvaluator = region.getObject( mesh2Name )
        val meshEvaluator : Evaluator = region.getObject( evaluatorName )
        val mesh1Type = mesh1Variable.valueType.asInstanceOf[MeshType]
        val mesh2Type = mesh2Variable.valueType.asInstanceOf[MeshType]
        val element1Count = mesh1Type.elementType.bounds.elementCount
        val element2Count = mesh2Type.elementType.bounds.elementCount

        val xyzArray = new StringBuilder()
        val polygonBlock = new StringBuilder()
        var elementNumber = 0
        for( element1Number <- 1 to element1Count )
        {
            for( element2Number <- 1 to element2Count )
            {
                for( i <- 0 to discretisation )
                {
                    for( j <- 0 to discretisation )
                    {
                        val xi1 : Double = i * 1.0 / discretisation
                        val xi2 : Double = j * 1.0 / discretisation

                        region.bind( mesh1Variable, element1Number, xi1 )
                        region.bind( mesh2Variable, element2Number, xi2 )

                        val value = region.evaluate( meshEvaluator )
                        appendTriple( value, xyzArray )
                    }
                }
                xyzArray.append( "\n" )

                elementNumber = elementNumber + 1

                val nodeOffsetOfElement = ( elementNumber - 1 ) * ( discretisation + 1 ) * ( discretisation + 1 )
                for( i <- 0 until discretisation )
                {
                    for( j <- 0 until discretisation )
                    {
                        val nodeAtLowerXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 0 )
                        val nodeAtLowerXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 0 ) + ( j + 1 )
                        val nodeAtUpperXi1UpperXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 1 )
                        val nodeAtUpperXi1LowerXi2 = nodeOffsetOfElement + ( discretisation + 1 ) * ( i + 1 ) + ( j + 0 )
                        polygonBlock.append( "<p>" )
                        polygonBlock.append( " " + nodeAtLowerXi1LowerXi2 )
                        polygonBlock.append( " " + nodeAtLowerXi1UpperXi2 )
                        polygonBlock.append( " " + nodeAtUpperXi1UpperXi2 )
                        polygonBlock.append( " " + nodeAtUpperXi1LowerXi2 )
                        polygonBlock.append( "</p>\n" )
                    }
                }
            }
        }

        val polygonCount = discretisation * discretisation * elementNumber
        val vertexCount = ( discretisation + 1 ) * ( discretisation + 1 ) * elementNumber
        val xyzArrayCount = vertexCount * 3

        val colladaString = fillInColladaTemplate( xyzArray, polygonBlock, polygonCount, vertexCount, xyzArrayCount )

        return colladaString
    }


    def export1DFromFieldML( region : Region, meshName : String, evaluatorName : String, discretisation : Int ) : String =
    {
        val meshVariable : AbstractEvaluator = region.getObject( meshName )
        val meshType = meshVariable.valueType.asInstanceOf[MeshType]
        val meshEvaluator : Evaluator = region.getObject( evaluatorName )
        val elementCount = meshType.elementType.bounds.elementCount
        val deltaX = 1.0 / discretisation
        var x = deltaX

        val xyzArray = new StringBuilder()
        val polygonBlock = new StringBuilder()
        for( elementNumber <- 1 to elementCount )
        {
            x -= deltaX
            for( j <- 0 to discretisation )
            {
                val xi1 : Double = j * 1.0 / discretisation

                region.bind( meshVariable, elementNumber, xi1 )
                
                val value = region.evaluate( meshEvaluator )
                appendSingle( value, x, 0.0, xyzArray )
                appendSingle( value, x, 1.0, xyzArray )
                
                x += deltaX
            }
            xyzArray.append( "\n" )

            val nodeOffsetOfElement = ( elementNumber - 1 ) * ( discretisation + 1 ) * 2

            for( j <- 0 until discretisation )
            {
                val nodeAtLowerXi1LowerXi2 = nodeOffsetOfElement + ( j * 2 ) + 0
                val nodeAtLowerXi1UpperXi2 = nodeOffsetOfElement + ( j * 2 ) + 2
                val nodeAtUpperXi1UpperXi2 = nodeOffsetOfElement + ( j * 2 ) + 3
                val nodeAtUpperXi1LowerXi2 = nodeOffsetOfElement + ( j * 2 ) + 1
                polygonBlock.append( "<p>" )
                polygonBlock.append( " " + nodeAtLowerXi1LowerXi2 )
                polygonBlock.append( " " + nodeAtLowerXi1UpperXi2 )
                polygonBlock.append( " " + nodeAtUpperXi1UpperXi2 )
                polygonBlock.append( " " + nodeAtUpperXi1LowerXi2 )
                polygonBlock.append( "</p>\n" )
            }
        }

        val polygonCount = discretisation * elementCount
        val vertexCount = ( discretisation + 1 ) * 2 * elementCount
        val xyzArrayCount = vertexCount * 3

        val colladaString = fillInColladaTemplate( xyzArray, polygonBlock, polygonCount, vertexCount, xyzArrayCount )

        return colladaString
    }
}
