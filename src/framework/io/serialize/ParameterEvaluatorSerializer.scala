package framework.io.serialize

import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType

import framework.datastore._

import fieldml.evaluator.ParameterEvaluator

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType
import fieldml.jni.DataLocationType
import fieldml.jni.DataFileType

class ParameterEvaluatorSerializer( val evaluator : ParameterEvaluator )
{
    private def insertSemidense( handle : Long, objectHandle : Int, description : SemidenseDataDescription ) : Unit =
    {
        Fieldml_SetParameterDataDescription( handle, objectHandle, DataDescriptionType.DESCRIPTION_SEMIDENSE )
        
        for( index <- description.sparseIndexes )
        {
            val indexHandle = GetNamedObject( handle, index.name )
            Fieldml_AddSemidenseIndex( handle, objectHandle, indexHandle, 1 )
        }
        for( index <- description.denseIndexes )
        {
            val indexHandle = GetNamedObject( handle, index.name )
            Fieldml_AddSemidenseIndex( handle, objectHandle, indexHandle, 0 )
        }
    }
    
    
    private def insertFileData( handle : Long, objectHandle : Int, location : FileDataLocation ) : Unit =
    {
        Fieldml_SetParameterDataLocation( handle, objectHandle, DataLocationType.LOCATION_FILE )
        location.dataType match
        {
            case DataType.TEXT_LINES => Fieldml_SetParameterFileData( handle, objectHandle, location.filename, DataFileType.TYPE_LINES, location.offset )
            case DataType.TEXT => Fieldml_SetParameterFileData( handle, objectHandle, location.filename, DataFileType.TYPE_TEXT, location.offset )
            case unknown => println( "Cannot yet serialize file data type " + unknown ) 
        }
    }
    
    
    private def insertInlineData( handle : Long, objectHandle : Int, location : InlineDataLocation ) : Unit =
    {
        Fieldml_SetParameterDataLocation( handle, objectHandle, DataLocationType.LOCATION_INLINE )
    }

    
    def insert( handle : Long ) : Unit =
    {
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        var objectHandle = FML_INVALID_HANDLE
        
        evaluator.valueType match
        {
            case d : ContinuousType => objectHandle = Fieldml_CreateContinuousParameters( handle, evaluator.name, valueHandle )
            case d : EnsembleType => objectHandle = Fieldml_CreateEnsembleParameters( handle, evaluator.name, valueHandle )
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
}
