package framework.io.serialize

import fieldml.DataResource

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataResourceType._
import fieldml.TextFileDataResource
import fieldml.InlineDataResource
import fieldml.DataSource

import framework.region.UserRegion


object DataResourceSerializer
{
    def insert( handle : Int, dataResource : DataResource ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( dataResource.isInstanceOf[TextFileDataResource] )
        {
            val textfile = dataResource.asInstanceOf[TextFileDataResource]
            val objectHandle = Fieldml_CreateTextFileDataResource( handle, textfile.name, textfile.href )
        }
        else if( dataResource.isInstanceOf[InlineDataResource] )
        {
            val inline = dataResource.asInstanceOf[InlineDataResource]
            val objectHandle = Fieldml_CreateTextInlineDataResource( handle, inline.name )
            Fieldml_AddInlineData( handle, objectHandle, inline.data, inline.data.length );
        }
    }

    
    private def extractTextFileResource( handle : Int, objectHandle : Int ) : DataResource =
    {
        val name = Fieldml_GetObjectName( handle, objectHandle )
        val href = Fieldml_GetDataResourceHref( handle, objectHandle )
        
        new TextFileDataResource( name, href )
    }
    
    
    private def extractInlineResource( handle : Int, objectHandle : Int ) : DataResource =
    {
        new InlineDataResource( Fieldml_GetInlineData( handle, objectHandle ) )
    }
    

    def extract( source : Deserializer, objectHandle : Int ) : DataResource =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val sourceType = Fieldml_GetDataResourceType( source.fmlHandle, objectHandle )
        
        sourceType match
        {
            case DATA_RESOURCE_TEXT_FILE => extractTextFileResource( source.fmlHandle, objectHandle )
            case DATA_RESOURCE_TEXT_INLINE => extractInlineResource( source.fmlHandle, objectHandle )
        }
    }
}