package framework.io.serialize

import fieldml.valueType.ValueType
import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType

import framework.datastore._

import fieldml.evaluator.Evaluator
import fieldml.evaluator.ParameterEvaluator

import fieldml.jni.DataFileType
import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType
import fieldml.jni.DataLocationType
import fieldml.jni.DataFileType

import framework.valuesource.ParameterEvaluatorValueSource

import util.exception._

object ParameterEvaluatorSerializer
{
    private def insertSemidense( handle : Long, objectHandle : Int, description : SemidenseDataDescription ) : Unit =
    {
        Fieldml_SetParameterDataDescription( handle, objectHandle, DataDescriptionType.DESCRIPTION_SEMIDENSE )
        
        for( index <- description.sparseIndexes )
        {
            val indexHandle = GetNamedObject( handle, index.name )
            Fieldml_AddSemidenseIndexEvaluator( handle, objectHandle, indexHandle, 1 )
        }
        for( index <- description.denseIndexes )
        {
            val indexHandle = GetNamedObject( handle, index.name )
            Fieldml_AddSemidenseIndexEvaluator( handle, objectHandle, indexHandle, 0 )
        }
    }
    
    
    private def insertFileData( handle : Long, objectHandle : Int, location : FileDataLocation ) : Unit =
    {
        Fieldml_SetParameterDataLocation( handle, objectHandle, DataLocationType.LOCATION_FILE )
        location.dataType match
        {
            case DataFileType.TYPE_LINES => Fieldml_SetParameterFileData( handle, objectHandle, location.filename, DataFileType.TYPE_LINES, location.offset )
            case DataFileType.TYPE_TEXT => Fieldml_SetParameterFileData( handle, objectHandle, location.filename, DataFileType.TYPE_TEXT, location.offset )
            case unknown => println( "Cannot yet serialize file data type " + unknown ) 
        }
    }
    
    
    private def insertInlineData( handle : Long, objectHandle : Int, location : InlineDataLocation ) : Unit =
    {
        Fieldml_SetParameterDataLocation( handle, objectHandle, DataLocationType.LOCATION_INLINE )
    }

    
    def insert( handle : Long, evaluator : ParameterEvaluator  ) : Unit =
    {
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        var objectHandle = FML_INVALID_HANDLE
        
        evaluator.valueType match
        {
            case d : ContinuousType => objectHandle = Fieldml_CreateParametersEvaluator( handle, evaluator.name, valueHandle )
            case d : EnsembleType => objectHandle = Fieldml_CreateParametersEvaluator( handle, evaluator.name, valueHandle )
        }
        
        evaluator.dataStore.description match
        {
            case d : SemidenseDataDescription => insertSemidense( handle, objectHandle, d )
            case unknown => println( "Cannot yet serialize data description " + unknown ) 
        }
        
        evaluator.dataStore.location match
        {
            case l : FileDataLocation => insertFileData( handle, objectHandle, l )
            case l : InlineDataLocation => insertInlineData( handle, objectHandle, l )
        }
    }
    
    
    private def extractSemidense( source : Deserializer, objectHandle : Int, valueType : ValueType ) : SemidenseDataDescription =
    {
        val sparseCount = Fieldml_GetSemidenseIndexCount( source.fmlHandle, objectHandle, 1 )
        val sparseIndexes = new Array[Evaluator]( sparseCount )
        
        for( i <- 1 to sparseCount )
        {
            sparseIndexes( i ) = source.getEvaluator( Fieldml_GetSemidenseIndexEvaluator( source.fmlHandle, objectHandle, i, 1 ) )
        }
        
        val denseCount = Fieldml_GetSemidenseIndexCount( source.fmlHandle, objectHandle, 1 )
        val denseIndexes = new Array[Evaluator]( denseCount )
        
        for( i <- 1 to denseCount )
        {
            denseIndexes( i ) = source.getEvaluator( Fieldml_GetSemidenseIndexEvaluator( source.fmlHandle, objectHandle, i, 1 ) )
        }
        
        val semidense = new SemidenseDataDescription( valueType, denseIndexes, sparseIndexes )
        
        val reader = Fieldml_OpenReader( source.fmlHandle, objectHandle )
        
        if( valueType.isInstanceOf[EnsembleType] )
        {
            initializeSemidenseIntValues( source, reader, semidense )
        }
        else if( valueType.isInstanceOf[ContinuousType] )
        {
            initializeSemidenseDoubleValues( source, reader, semidense )
        }
        else
        {
            throw new FmlException( "Cannot yet initialize " + valueType.name + " valued parameter evaluator" )
        }
        
        Fieldml_CloseReader( source.fmlHandle, reader )
        
        semidense
    }
    
    
    private def extractInlineData( source : Deserializer, objectHandle : Int ) : InlineDataLocation =
    {
        new InlineDataLocation( Fieldml_GetParameterInlineData( source.fmlHandle, objectHandle ) )
    }
    
    
    private def extractFileData( source : Deserializer, objectHandle : Int ) : FileDataLocation =
    {
        val filename = Fieldml_GetParameterDataFilename( source.fmlHandle, objectHandle )
        val offset = Fieldml_GetParameterDataOffset( source.fmlHandle, objectHandle )
        val filetype = Fieldml_GetParameterDataFileType( source.fmlHandle, objectHandle )
        
        new FileDataLocation( filename, offset, filetype ) 
    }
    
    
    def extract( source : Deserializer, objectHandle : Int ) : ParameterEvaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        val valueType = source.getContinuousType( typeHandle )

        val dataDescription = Fieldml_GetParameterDataDescription( source.fmlHandle, objectHandle ) match
        {
            case DataDescriptionType.DESCRIPTION_SEMIDENSE => extractSemidense( source, objectHandle, valueType )
            case d => throw new FmlException( "Unsupported data description: " + d ) 
        }
        
        val dataLocation = Fieldml_GetParameterDataLocation( source.fmlHandle, objectHandle ) match
        {
            case DataLocationType.LOCATION_INLINE => extractInlineData( source, objectHandle )
            case DataLocationType.LOCATION_FILE => extractFileData( source, objectHandle )
            case l => throw new FmlException( "Unsupported data location: " + l ) 
        }
        
        val dataStore = new DataStore( dataLocation, dataDescription ) 

        val parameterEval = new ParameterEvaluatorValueSource( name, valueType, dataStore )
        
        parameterEval
    }
}
