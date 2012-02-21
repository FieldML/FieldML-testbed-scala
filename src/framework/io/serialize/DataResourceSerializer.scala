package framework.io.serialize

import fieldml.DataResource
import util.exception._
import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlDataResourceType._
import fieldml.TextFileDataResource
import fieldml.InlineDataResource
import fieldml.DataSource
import framework.region.UserRegion
import fieldml.Hdf5FileDataResource


object DataResourceSerializer
{
    def insert( handle : Int, dataResource : DataResource ) : Unit =
    {
        var componentHandle = FML_INVALID_HANDLE
        
        if( dataResource.isInstanceOf[TextFileDataResource] )
        {
            val textfile = dataResource.asInstanceOf[TextFileDataResource]
            val objectHandle = Fieldml_CreateHrefDataResource( handle, textfile.name, "PLAIN_TEXT", textfile.href )
        }
        else if( dataResource.isInstanceOf[Hdf5FileDataResource] )
        {
            val hdf5file = dataResource.asInstanceOf[Hdf5FileDataResource]
            val objectHandle = Fieldml_CreateHrefDataResource( handle, hdf5file.name, "HDF5", hdf5file.href )
        }
        else if( dataResource.isInstanceOf[InlineDataResource] )
        {
            val inline = dataResource.asInstanceOf[InlineDataResource]
            val objectHandle = Fieldml_CreateInlineDataResource( handle, inline.name )
            Fieldml_AddInlineData( handle, objectHandle, inline.data, inline.data.length );
        }
    }

    
    private def extractFileResource( handle : Int, objectHandle : Int ) : DataResource =
    {
        val name = Fieldml_GetObjectName( handle, objectHandle )
        val href = Fieldml_GetDataResourceHref( handle, objectHandle )
        
        val format = Fieldml_GetDataResourceFormat( handle, objectHandle )
        
        if( format == "HDF5" )
        {
            new Hdf5FileDataResource( name, href )
        }
        else if( format == "PLAIN_TEXT" )
        {
            new TextFileDataResource( name, href )
        }
        else
        {
            null;
        }
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
            case FML_DATA_RESOURCE_HREF => extractFileResource( source.fmlHandle, objectHandle )
            case FML_DATA_RESOURCE_INLINE => extractInlineResource( source.fmlHandle, objectHandle )
        }
    }
}
