package framework.io.serialize

import fieldml.DataObject

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataSourceType._

import framework.region.UserRegion
import framework.datastore.TextFileDataSource
import framework.datastore.InlineDataSource
import framework.datastore.DataSource


object DataObjectSerializer
{
    def insert( handle : Int, dataObject : DataObject ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        val objectHandle = Fieldml_CreateDataObject( handle, dataObject.name )
        
        if( dataObject.dataSource.isInstanceOf[TextFileDataSource] )
        {
            val textfile = dataObject.dataSource.asInstanceOf[TextFileDataSource]
            Fieldml_SetDataObjectSourceType( handle, objectHandle, SOURCE_TEXT_FILE )
            Fieldml_SetDataObjectTextFileInfo( handle, objectHandle, textfile.filename, textfile.offset )
        }
        else if( dataObject.dataSource.isInstanceOf[InlineDataSource] )
        {
            val inline = dataObject.dataSource.asInstanceOf[InlineDataSource]
            Fieldml_SetDataObjectSourceType( handle, objectHandle, SOURCE_INLINE )
            Fieldml_AddInlineData( handle, objectHandle, inline.data, inline.data.length );
        }
    }

    
    private def extractTextFileSource( handle : Int, objectHandle : Int ) : DataSource =
    {
        val filename = Fieldml_GetDataObjectFilename( handle, objectHandle )
        val offset = Fieldml_GetDataObjectFileOffset( handle, objectHandle )
        
        new TextFileDataSource( filename, offset )
    }
    
    
    private def extractInlineSource( handle : Int, objectHandle : Int ) : DataSource =
    {
        new InlineDataSource( Fieldml_GetInlineData( handle, objectHandle ) )
    }
    

    def extract( source : Deserializer, objectHandle : Int ) : DataObject =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val sourceType = Fieldml_GetDataObjectSourceType( source.fmlHandle, objectHandle )
        
        val dataSource = sourceType match
        {
            case SOURCE_TEXT_FILE => extractTextFileSource( source.fmlHandle, objectHandle )
            case SOURCE_INLINE => extractInlineSource( source.fmlHandle, objectHandle )
        }
        
        val entryCount = Fieldml_GetDataObjectEntryCount( source.fmlHandle, objectHandle )
        val entryLength = Fieldml_GetDataObjectEntryLength( source.fmlHandle, objectHandle )
        val entryHead = Fieldml_GetDataObjectEntryHead( source.fmlHandle, objectHandle )
        val entryTail = Fieldml_GetDataObjectEntryTail( source.fmlHandle, objectHandle )
        
        return new DataObject( name, dataSource, entryCount, entryLength, entryHead, entryTail )
    }
}