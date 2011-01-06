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
    private class IndexIterator( private val ensembles : Array[EnsembleType] )
    {
        private val indexes = for( e <- ensembles ) yield 1
        
        private var _hasNext = true
        
        def hasNext = _hasNext
        
        def next() : Array[Int] =
        {
            if( !hasNext )
            {
                throw new FmlException( "IndexIterator overrun" )
            }
            
            val next = indexes.clone

            var incIdx = 0
            while( ( incIdx < indexes.length ) && ( indexes( incIdx ) == ensembles( incIdx ).bounds.elementCount ) )
            {
                incIdx += 1
            }
            
            if( incIdx == indexes.length )
            {
                _hasNext = false
            }
            else
            {
                for( i <- 0 until incIdx )
                {
                    indexes( i ) = 1
                }
                indexes( incIdx ) += 1
            }
         
            return next
        }
    }
    
    
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
    
    
    private def writeIntDataStore( handle : Long, objectHandle : Int, dataStore : DataStore ) : Unit =
    {
        val semidense = dataStore.description.asInstanceOf[SemidenseDataDescription]
        val writer = Fieldml_OpenWriter( handle, objectHandle, 0 )
        val iterator = new IndexIterator( semidense.denseIndexes.map( _.valueType.asInstanceOf[EnsembleType] ) )

        val minCount = getMinCount( semidense )
        val slices = 20
        
        val buffer = new Array[Int]( minCount * slices )
        
        var sliceCount = 0
        var count = 0
        var total = 0
        
        while( iterator.hasNext )
        {
            sliceCount = 0
            while( iterator.hasNext && ( sliceCount < slices ) )
            {
                for( i <- 0 until minCount )
                {
                    buffer( i + (sliceCount * minCount) ) = semidense( iterator.next ).get.eValue
                }
                sliceCount += 1
            }
            
            val count = Fieldml_WriteIntValues( handle, writer, buffer, minCount * sliceCount )
            if( count != minCount * sliceCount )
            {
                throw new FmlException( "Write error in semidense data after " + total )
            }
            
            total += count
        }
        
        Fieldml_CloseWriter( handle, writer )
    }
    
    
    private def writeDoubleDataStore( handle : Long, objectHandle : Int, dataStore : DataStore ) : Unit =
    {
        val semidense = dataStore.description.asInstanceOf[SemidenseDataDescription]
        val writer = Fieldml_OpenWriter( handle, objectHandle, 0 )
        val iterator = new IndexIterator( semidense.denseIndexes.map( _.valueType.asInstanceOf[EnsembleType] ) )

        val minCount = getMinCount( semidense )
        val slices = 20
        
        val buffer = new Array[Double]( minCount * slices )
        
        var sliceCount = 0
        var count = 0
        var total = 0
        
        while( iterator.hasNext )
        {
            sliceCount = 0
            while( iterator.hasNext && ( sliceCount < slices ) )
            {
                for( i <- 0 until minCount )
                {
                    buffer( i + (sliceCount * minCount) ) = semidense( iterator.next ).get.cValue( 0 )
                }
                sliceCount += 1
            }
            
            val count = Fieldml_WriteDoubleValues( handle, writer, buffer, minCount * sliceCount )
            if( count != minCount * sliceCount )
            {
                throw new FmlException( "Write error in semidense data after " + total + ": " + writer + " ... " + count + " != " + ( minCount * sliceCount ) )
            }
            
            total += count
        }
        
        Fieldml_CloseWriter( handle, writer )
    }

    
    def insert( handle : Long, evaluator : ParameterEvaluator  ) : Unit =
    {
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        var objectHandle = Fieldml_CreateParametersEvaluator( handle, evaluator.name, valueHandle )

        evaluator.dataStore.description match
        {
            case d : SemidenseDataDescription => insertSemidense( handle, objectHandle, d )
            case unknown => println( "Cannot yet serialize data description " + unknown ); return 
        }
        
        val semidense = evaluator.dataStore.description.asInstanceOf[SemidenseDataDescription]
        if( semidense.sparseIndexes.length > 0 )
        {
            println( "Cannot yet serialize semidata with sparse indexes" )
            return
        }
        
        evaluator.dataStore.location match
        {
            case l : FileDataLocation => insertFileData( handle, objectHandle, l )
            case l : InlineDataLocation => insertInlineData( handle, objectHandle, l )
        }
        
        evaluator.valueType match
        {
            case d : ContinuousType => writeDoubleDataStore( handle, objectHandle, evaluator.dataStore )
            case d : EnsembleType => writeIntDataStore( handle, objectHandle, evaluator.dataStore )
        }
    }
    
    
    private def getMinCount( semidense : SemidenseDataDescription ) : Int =
    {
        if( semidense.denseIndexes.length == 0 )
        {
            return 1
        }
        
        val indexType = semidense.denseIndexes( 0 ).valueType.asInstanceOf[EnsembleType]
        
        println( "SD " + semidense.denseIndexes( 0 ).name + " " + indexType.isComponent )
        
        if( !indexType.isComponent )
        {
            return 1
        }
        
        return indexType.bounds.elementCount
    }

    
    private def initializeSemidenseIntValues( source : Deserializer, reader : Long, semidense : SemidenseDataDescription ) : Unit =
    {
        val minCount = getMinCount( semidense )
        val slices = 20
        
        val buffer = new Array[Int]( minCount * slices )
        val indexes = new Array[Int]( semidense.sparseIndexes.length )
        
        var err = 0
        var total = 0
        
        while( Fieldml_ReadIndexSet( source.fmlHandle, reader, indexes ) == FML_ERR_NO_ERROR )
        {
            val iterator = new IndexIterator( semidense.denseIndexes.map( _.valueType.asInstanceOf[EnsembleType] ) )

            while( iterator.hasNext )
            {
                val count = Fieldml_ReadIntValues( source.fmlHandle, reader, buffer, minCount * slices )
                if( count < 0 )
                {
                    throw new FmlException( "Read error in semidense data after " + total )
                }
                if( ( count == 0 ) && ( iterator.hasNext ) )
                {
                    throw new FmlException( "Ran out of dense data after " + total )
                }
                
                total += count
                
                if( count % minCount != 0 )
                {
                    throw new FmlException( "API failure: Invalid read count." )
                }
                
                for( i <- 0 until count )
                {
                    semidense( iterator.next ) = buffer( i )
                }
            }
        }
    }

    
    private def initializeSemidenseDoubleValues( source : Deserializer, reader : Long, semidense : SemidenseDataDescription ) : Unit =
    {
        val minCount = getMinCount( semidense )
        val slices = 20
        
        val buffer = new Array[Double]( minCount * slices )
        val indexes = new Array[Int]( semidense.sparseIndexes.length )
        
        var err = 0
        var total = 0
        
        while( Fieldml_ReadIndexSet( source.fmlHandle, reader, indexes ) == FML_ERR_NO_ERROR )
        {
            val iterator = new IndexIterator( semidense.denseIndexes.map( _.valueType.asInstanceOf[EnsembleType] ) )

            while( iterator.hasNext )
            {
                val count = Fieldml_ReadDoubleValues( source.fmlHandle, reader, buffer, minCount * slices )
                if( count < 0 )
                {
                    throw new FmlException( "Read error in semidense data after " + total )
                }
                if( ( count == 0 ) && ( iterator.hasNext ) )
                {
                    throw new FmlException( "Ran out of dense data after " + total )
                }
                
                total += count
                
                if( count % minCount != 0 )
                {
                    throw new FmlException( "API failure: Invalid read count." )
                }
                
                for( i <- 0 until count )
                {
                    semidense( iterator.next ) = buffer( i )
                }
            }
        }
    }
    
    
    private def extractSemidense( source : Deserializer, objectHandle : Int, valueType : ValueType ) : SemidenseDataDescription =
    {
        val sparseCount = Fieldml_GetSemidenseIndexCount( source.fmlHandle, objectHandle, 1 )
        val sparseIndexes = new Array[Evaluator]( sparseCount )
        
        for( i <- 1 to sparseCount )
        {
            sparseIndexes( i - 1 ) = source.getEvaluator( Fieldml_GetSemidenseIndexEvaluator( source.fmlHandle, objectHandle, i, 1 ) )
        }
        
        val denseCount = Fieldml_GetSemidenseIndexCount( source.fmlHandle, objectHandle, 0 )
        val denseIndexes = new Array[Evaluator]( denseCount )
        
        for( i <- 1 to denseCount )
        {
            denseIndexes( i - 1 ) = source.getEvaluator( Fieldml_GetSemidenseIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) )
        }
        
        val semidense = new SemidenseDataDescription( valueType, denseIndexes, sparseIndexes )
        
        val reader = Fieldml_OpenReader( source.fmlHandle, objectHandle )
        if( reader == 0 )
        {
            throw new FmlException( "Cannot create semidense reader"  )
        }
        
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
        val valueType = source.getType( typeHandle )

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
