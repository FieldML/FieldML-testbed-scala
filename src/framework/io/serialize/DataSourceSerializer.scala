package framework.io.serialize

import fieldml.DataSource

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataSourceType._
import fieldml.TextDataSource
import fieldml.DataSource

import framework.region.UserRegion


object DataSourceSerializer
{
    def insert( handle : Int, dataSource : DataSource ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( dataSource.isInstanceOf[TextDataSource] )
        {
            val textSource = dataSource.asInstanceOf[TextDataSource]
            val resourceHandle = GetNamedObject( handle, textSource.dataResource.name )
            val objectHandle = Fieldml_CreateTextDataSource( handle, dataSource.name, resourceHandle, textSource.firstLine, textSource.entryCount, textSource.entryLength, textSource.entryHead, textSource.entryTail )
        }
    }

    
    private def extractTextDataSource( source : Deserializer, objectHandle : Int ) : DataSource =
    {
        val resourceHandle = Fieldml_GetDataSourceResource( source.fmlHandle, objectHandle )
        val resource = source.getDataResource( resourceHandle )
        
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        val firstLine = Fieldml_GetTextDataSourceFirstLine( source.fmlHandle, objectHandle )
        val count = Fieldml_GetTextDataSourceCount( source.fmlHandle, objectHandle )
        val length = Fieldml_GetTextDataSourceLength( source.fmlHandle, objectHandle )
        val head = Fieldml_GetTextDataSourceHead( source.fmlHandle, objectHandle )
        val tail = Fieldml_GetTextDataSourceTail( source.fmlHandle, objectHandle )
        
        new TextDataSource( name, resource, firstLine, count, length, head, tail )
    }
    
    
    def extract( source : Deserializer, objectHandle : Int ) : DataSource =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val sourceType = Fieldml_GetDataSourceType( source.fmlHandle, objectHandle )
        
        sourceType match
        {
            case DATA_SOURCE_TEXT => extractTextDataSource( source, objectHandle )
        }
    }
}
