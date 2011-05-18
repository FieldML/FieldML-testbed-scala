package framework.io.serialize

import fieldml.valueType.ValueType
import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType

import framework.datastore._

import fieldml.evaluator.Evaluator
import fieldml.evaluator.ParameterEvaluator

import fieldml.jni.DataSourceType
import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType

import framework.valuesource.ParameterEvaluatorValueSource

import util.exception._

object ParameterEvaluatorSerializer
{
    private class IndexIterator( private val ensembles : Array[()=>BufferedIterator[Int]] )
    {
        private var indexes : Array[BufferedIterator[Int]] = null
        
        private var _hasNext = true

        reset()
        
        def hasNext = indexes.last.hasNext
        
        def next() : Array[Int] =
        {
            if( !hasNext )
            {
                throw new FmlException( "IndexIterator overrun" )
            }

            val values = indexes.map( _.head )

            indexes( 0 ).next
            var incIdx = 0
            while( ( incIdx < indexes.length - 1 ) && !indexes( incIdx ).hasNext )
            {
                indexes( incIdx ) = ensembles( incIdx )()
                incIdx = incIdx + 1
                if( incIdx < indexes.length )
                {
                    indexes( incIdx ).next
                }
            }
            
            return values
        }
        
        def reset()
        {
            indexes = for( e <- ensembles ) yield e()
            _hasNext = true
        }
    }
    
    
    private def insertSemidense( handle : Int, objectHandle : Int, description : SemidenseDataDescription ) : Unit =
    {
        Fieldml_SetParameterDataDescription( handle, objectHandle, DataDescriptionType.DESCRIPTION_SEMIDENSE )
        
        for( index <- description.sparseIndexes )
        {
            val indexHandle = GetNamedObject( handle, index.name )
            Fieldml_AddSparseIndexEvaluator( handle, objectHandle, indexHandle )
        }
        for( index <- description.denseIndexes )
        {
            val indexHandle = GetNamedObject( handle, index.name )
            Fieldml_AddDenseIndexEvaluator( handle, objectHandle, indexHandle, FML_INVALID_HANDLE )
        }
    }
    
    
    /*
    private def writeIntDataStore( handle : Int, objectHandle : Int, dataStore : DataStore ) : Unit =
    {
        val semidense = dataStore.description.asInstanceOf[SemidenseDataDescription]
        val writer = Fieldml_OpenWriter( handle, objectHandle, 0 )
        if( writer == FML_INVALID_HANDLE )
        {
            //Writer error. Just skip it for now.
            return;
        }
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
    
    
    private def writeDoubleDataStore( handle : Int, objectHandle : Int, dataStore : DataStore ) : Unit =
    {
        val semidense = dataStore.description.asInstanceOf[SemidenseDataDescription]
        val writer = Fieldml_OpenWriter( handle, objectHandle, 0 )
        if( writer == FML_INVALID_HANDLE )
        {
            //Writer error. Just skip it for now.
            return;
        }
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
    */

    
    def insert( handle : Int, evaluator : ParameterEvaluator  ) : Unit =
    {
        val valueHandle = GetNamedObject( handle, evaluator.valueType.name )
        
        val objectHandle = Fieldml_CreateParameterEvaluator( handle, evaluator.name, valueHandle )
        
        val dataHandle = GetNamedObject( handle, evaluator.dataStore.source.name )

        Fieldml_SetDataSource( handle, objectHandle, dataHandle )

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
    }
    
    
    private def getMinCount( semidense : SemidenseDataDescription ) : Int =
    {
        if( semidense.denseIndexes.length == 0 )
        {
            return 1
        }
        
        val indexType = semidense.denseIndexes( 0 ).valueType.asInstanceOf[EnsembleType]
        
        if( !indexType.isComponent )
        {
            return 1
        }
        
        return indexType.elementCount
    }

    
    private def initializeSemidenseIntValues( source : Deserializer, reader : Int, semidense : SemidenseDataDescription ) : Unit =
    {
        val minCount = getMinCount( semidense )
        val slices = 20
        val indexCount = semidense.sparseIndexes.length
        
        val buffer = new Array[Int]( minCount * slices )
        val indexes = new Array[Int]( indexCount )
        
        var err = 0
        var total = 0
        
        val generators = for( i <- 0 until semidense.denseIndexes.size ) yield semidense.denseOrders(i) match
        {
            case null => semidense.denseIndexes( i ).valueType.asInstanceOf[EnsembleType].elementSet.bufferedIterator _
            case o => o.iterator.buffered _
        }
        
        val iterator = new IndexIterator( generators.toArray )
        
        while( Fieldml_ReadIntValues( source.fmlHandle, reader, indexes, indexCount ) == FML_ERR_NO_ERROR )
        {
            iterator.reset()

            while( iterator.hasNext )
            {
                val count = Fieldml_ReadIntValues( source.fmlHandle, reader, buffer, minCount * slices )
                if( count < 0 )
                {
                    throw new FmlException( "Read error in semidense data from " + reader + " after " + total )
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

    
    private def initializeSemidenseDoubleValues( source : Deserializer, reader : Int, semidense : SemidenseDataDescription ) : Unit =
    {
        val minCount = getMinCount( semidense )
        val slices = 20
        val indexCount = semidense.sparseIndexes.length
        
        val buffer = new Array[Double]( minCount * slices )
        val indexes = new Array[Int]( indexCount )
        
        var err = 0
        var total = 0
        
        val generators = for( i <- 0 until semidense.denseIndexes.size ) yield semidense.denseOrders(i) match
        {
            case null => semidense.denseIndexes( i ).valueType.asInstanceOf[EnsembleType].elementSet.bufferedIterator _
            case o => o.iterator.buffered _
        }
        
        val iterator = new IndexIterator( generators.toArray )
        
        while( Fieldml_ReadIntValues( source.fmlHandle, reader, indexes, indexCount ) == FML_ERR_NO_ERROR )
        {
            iterator.reset()

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
    

    private def extractOrder( source : Deserializer, objectHandle : Int, count : Int ) : Array[Int] =
    {
        val reader = Fieldml_OpenReader( source.fmlHandle, objectHandle )
        
        val values = new Array[Int]( count )
        
        val readCount = Fieldml_ReadIntValues( source.fmlHandle, reader, values, count )
        
        Fieldml_CloseReader( source.fmlHandle, reader )
        
        return values
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
        val denseOrders = new Array[Array[Int]]( denseCount )
        
        for( i <- 1 to denseCount )
        {
            denseIndexes( i - 1 ) = source.getEvaluator( Fieldml_GetSemidenseIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) )
            val indexType = Fieldml_GetValueType( source.fmlHandle, Fieldml_GetSemidenseIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) );
            val ensembleCount = Fieldml_GetElementCount( source.fmlHandle, indexType );
            
            denseOrders( i - 1 ) = Fieldml_GetSemidenseIndexOrder( source.fmlHandle, objectHandle, i ) match
            {
                case FML_INVALID_HANDLE => null
                case handle => extractOrder( source, handle, ensembleCount )
            }
        }
        
        val semidense = new SemidenseDataDescription( valueType, denseOrders, denseIndexes, sparseIndexes )
        val dataHandle = Fieldml_GetDataSource( source.fmlHandle, objectHandle )
        
        val reader = Fieldml_OpenReader( source.fmlHandle, dataHandle )
        if( reader == FML_INVALID_HANDLE )
        {
            throw new FmlException( "Cannot create semidense reader: " + Fieldml_GetLastError( source.fmlHandle )  )
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
        
        val dataObjectHandle = Fieldml_GetDataSource( source.fmlHandle, objectHandle )
        val dataObject = source.getDataSource( dataObjectHandle )
        
        val dataStore = new DataStore( dataObject, dataDescription ) 

        val parameterEval = new ParameterEvaluatorValueSource( name, valueType, dataStore )
        
        parameterEval
    }
}
