package framework.io.serialize

import fieldml.DataSource

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlDataSourceType._
import fieldml.ArrayDataSource
import fieldml.DataSource

import framework.region.UserRegion


object DataSourceSerializer
{
    def insert( handle : Int, dataSource : DataSource ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( dataSource.isInstanceOf[ArrayDataSource] )
        {
            val arraySource = dataSource.asInstanceOf[ArrayDataSource]
            val resourceHandle = GetNamedObject( handle, arraySource.dataResource.name )
            val objectHandle = Fieldml_CreateArrayDataSource( handle, arraySource.name, resourceHandle, arraySource.location, arraySource.rank )
        }
    }

    
    private def extractArrayDataSource( source : Deserializer, objectHandle : Int ) : DataSource =
    {
        val resourceHandle = Fieldml_GetDataSourceResource( source.fmlHandle, objectHandle )
        val resource = source.getDataResource( resourceHandle )
        
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        val location = Fieldml_GetArrayDataSourceLocation( source.fmlHandle, objectHandle )
        val rank = Fieldml_GetArrayDataSourceRank( source.fmlHandle, objectHandle )
        
        new ArrayDataSource( name, resource, location, rank )
    }
    
    
    def extract( source : Deserializer, objectHandle : Int ) : DataSource =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val sourceType = Fieldml_GetDataSourceType( source.fmlHandle, objectHandle )
        
        sourceType match
        {
            case FML_DATA_SOURCE_ARRAY => extractArrayDataSource( source, objectHandle )
        }
    }
}
