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
import framework.value.Value

import util.exception._

object ParameterEvaluatorSerializer
{
    private class IndexIterator( private val ensembles : Array[()=>BufferedIterator[Int]] )
    {
        private var indexes : Array[BufferedIterator[Int]] = null
        
        private var _hasNext = true

        reset()
        
        def hasNext = indexes( 0 ).hasNext
        
        def next() : Array[Int] =
        {
            if( !hasNext )
            {
                throw new FmlException( "IndexIterator overrun" )
            }

            val values = indexes.map( _.head )

            indexes.last.next
            var incIdx = indexes.length - 1
            while( ( incIdx > 0 ) && !indexes( incIdx ).hasNext )
            {
                indexes( incIdx ) = ensembles( incIdx )()
                incIdx = incIdx - 1
                if( incIdx >= 0 )
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
    
    
    private def insertDok( handle : Int, objectHandle : Int, description : DokDataDescription ) : Unit =
    {
        Fieldml_SetParameterDataDescription( handle, objectHandle, DataDescriptionType.DESCRIPTION_DOK_ARRAY )
        
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
    
    
    private def insertDense( handle : Int, objectHandle : Int, description : DenseDataDescription ) : Unit =
    {
        Fieldml_SetParameterDataDescription( handle, objectHandle, DataDescriptionType.DESCRIPTION_DENSE_ARRAY )
        
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
            case d : DokDataDescription => insertDok( handle, objectHandle, d )
            case d : DenseDataDescription => insertDense( handle, objectHandle, d )
            case unknown => println( "Cannot yet serialize data description " + unknown ); return 
        }
        
        //TODO Serialize the datastore's data.
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

    
    private def initializeDokValues[T<:AnyVal:Manifest]( source : Deserializer, valueReader : Int, keyReader : Int, dok : DokDataDescription,
        SlabReader : ( Int, Int, Array[Int], Array[Int], Array[T] ) => Int,
        ValueGenerator : ( ValueType, T* ) => Value ) : Unit =
    {
        val indexCount = dok.sparseIndexes.size
        val sizes : Array[Int] = Array.concat( Array( 1 ), dok.denseIndexes.map( _.valueType.asInstanceOf[EnsembleType].elementCount ).toArray )
        val offsets = Array.fill( dok.denseIndexes.size + 1 )( 0 )

        //TODO In theory, this may overflow.
        val bufferSize = sizes.reduceLeft( _*_ )
        val buffer = new Array[T]( bufferSize )
        val keys = new Array[Int]( indexCount )
        
        var err = 0
        var total = 0
        
        val generators = for( i <- 0 until dok.denseIndexes.size ) yield dok.denseOrders(i) match
        {
            case null => dok.denseIndexes( i ).valueType.asInstanceOf[EnsembleType].elementSet.bufferedIterator _
            case o => o.iterator.buffered _
        }
        
        val iterator = new IndexIterator( generators.toArray )
        
        val indexOffsets = Array[Int]( 0, 0 )
        val indexSizes = Array[Int]( 1, indexCount )
        
        while( Fieldml_ReadIntSlab( source.fmlHandle, keyReader, indexOffsets, indexSizes, keys ) == FML_ERR_NO_ERROR )
        {
            iterator.reset()

            err = SlabReader( source.fmlHandle, valueReader, offsets, sizes, buffer )
            if( err != FML_ERR_NO_ERROR )
            {
                throw new FmlException( "Read error in DOK value data after " + total + ": code " + err )
            }
            
            total += bufferSize
            
            for( i <- 0 until bufferSize )
            {
                dok( iterator.next ) = ValueGenerator( dok.valueType, buffer( i ) )
            }
            
            offsets( 0 ) = offsets( 0 ) + 1
            
            indexOffsets( 0 ) = indexOffsets( 0 ) + 1
        }
    }
    

    private def initializeDenseValues[T:Manifest]( source : Deserializer, reader : Int, dense : DenseDataDescription,
        SlabReader : ( Int, Int, Array[Int], Array[Int], Array[T] ) => Int,
        ValueGenerator : ( ValueType, T* ) => Value ) : Unit =
    {
        val sizes = dense.denseIndexes.map( _.valueType.asInstanceOf[EnsembleType].elementCount ).toArray
        val offsets = Array.fill( dense.denseIndexes.size )( 0 )

        //TODO In theory, this may overflow.
        val bufferSize = sizes.reduceLeft( _*_ )
        val buffer = new Array[T]( bufferSize )
        
        var err = 0
        
        val generators = for( i <- 0 until dense.denseIndexes.size ) yield dense.denseOrders(i) match
        {
            case null => dense.denseIndexes( i ).valueType.asInstanceOf[EnsembleType].elementSet.bufferedIterator _
            case o => o.iterator.buffered _
        }

        val iterator = new IndexIterator( generators.toArray )
        
        iterator.reset()

        while( iterator.hasNext )
        {
            err = SlabReader( source.fmlHandle, reader, offsets, sizes, buffer )
            
            println( "Offsets = " + offsets.deepToString )
            println( "Sizes = " + sizes.deepToString )
            println( "Read " + bufferSize + " values, err = " + err )

            if( err != FML_ERR_NO_ERROR )
            {
                throw new FmlException( "Read error in dense data from " + reader + ": code " + err )
            }
            
            for( i <- 0 until bufferSize )
            {
                dense( iterator.next ) = ValueGenerator( dense.valueType, buffer( i ) )
            }
        }
    }

    
    private def extractOrder( source : Deserializer, objectHandle : Int, count : Int ) : Array[Int] =
    {
        val rank = Fieldml_GetArrayDataSourceRank( source.fmlHandle, objectHandle )
        if( rank != 1 )
        {
            throw new FmlException( "Order arrays must be 1-dimensional" )
        }
        
        val reader = Fieldml_OpenReader( source.fmlHandle, objectHandle )
        
        val offsets = Array( 0 )
        val sizes = Array( count )
        val values = new Array[Int]( count )
        
        val readCount = Fieldml_ReadIntSlab( source.fmlHandle, reader, offsets, sizes, values )
        
        Fieldml_CloseReader( source.fmlHandle, reader )
        
        return values
    }
    
    
    private def extractDok( source : Deserializer, objectHandle : Int, valueType : ValueType ) : DokDataDescription =
    {
        val sparseCount = Fieldml_GetParameterIndexCount( source.fmlHandle, objectHandle, 1 )
        val sparseIndexes = new Array[Evaluator]( sparseCount )
        
        for( i <- 1 to sparseCount )
        {
            sparseIndexes( i - 1 ) = source.getEvaluator( Fieldml_GetParameterIndexEvaluator( source.fmlHandle, objectHandle, i, 1 ) )
        }
        
        val denseCount = Fieldml_GetParameterIndexCount( source.fmlHandle, objectHandle, 0 )
        val denseIndexes = new Array[Evaluator]( denseCount )
        val denseOrders = new Array[Array[Int]]( denseCount )
        
        for( i <- 1 to denseCount )
        {
            denseIndexes( i - 1 ) = source.getEvaluator( Fieldml_GetParameterIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) )
            val indexType = Fieldml_GetValueType( source.fmlHandle, Fieldml_GetParameterIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) );
            val ensembleCount = Fieldml_GetMemberCount( source.fmlHandle, indexType );
            
            denseOrders( i - 1 ) = Fieldml_GetParameterIndexOrder( source.fmlHandle, objectHandle, i ) match
            {
                case FML_INVALID_HANDLE => null
                case handle => extractOrder( source, handle, ensembleCount )
            }
        }
        
        val dok = new DokDataDescription( valueType, denseOrders, denseIndexes, sparseIndexes )
        val dataHandle = Fieldml_GetDataSource( source.fmlHandle, objectHandle )
        val keyDataHandle = Fieldml_GetKeyDataSource( source.fmlHandle, objectHandle )
        
        val valueReader = Fieldml_OpenReader( source.fmlHandle, dataHandle )
        if( valueReader == FML_INVALID_HANDLE )
        {
            throw new FmlException( "Cannot create DOK value reader: " + Fieldml_GetLastError( source.fmlHandle )  )
        }
        
        val keyReader = Fieldml_OpenReader( source.fmlHandle, dataHandle )
        if( keyReader == FML_INVALID_HANDLE )
        {
            throw new FmlException( "Cannot create DOK key reader: " + Fieldml_GetLastError( source.fmlHandle )  )
        }
        
        if( valueType.isInstanceOf[EnsembleType] )
        {
            val generator : ( ValueType, Int* ) => Value = ( a, b ) => Value.apply( a, b.map( _.toDouble ):_* ) 
            initializeDokValues( source, valueReader, keyReader, dok, Fieldml_ReadIntSlab, generator )
        }
        else if( valueType.isInstanceOf[ContinuousType] )
        {
            val generator : ( ValueType, Double* ) => Value = Value.apply
            initializeDokValues( source, valueReader, keyReader, dok, Fieldml_ReadDoubleSlab, generator )
        }
        else
        {
            throw new FmlException( "Cannot yet initialize " + valueType.name + " valued parameter evaluator" )
        }
        
        Fieldml_CloseReader( source.fmlHandle, valueReader )
        Fieldml_CloseReader( source.fmlHandle, keyReader )
        
        dok
    }
    
    
    private def extractDense( source : Deserializer, objectHandle : Int, valueType : ValueType ) : DenseDataDescription =
    {
        val denseCount = Fieldml_GetParameterIndexCount( source.fmlHandle, objectHandle, 0 )
        val denseIndexes = new Array[Evaluator]( denseCount )
        val denseOrders = new Array[Array[Int]]( denseCount )
        
        for( i <- 1 to denseCount )
        {
            denseIndexes( i - 1 ) = source.getEvaluator( Fieldml_GetParameterIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) )
            val indexType = Fieldml_GetValueType( source.fmlHandle, Fieldml_GetParameterIndexEvaluator( source.fmlHandle, objectHandle, i, 0 ) );
            val ensembleCount = Fieldml_GetMemberCount( source.fmlHandle, indexType );
            
            denseOrders( i - 1 ) = Fieldml_GetParameterIndexOrder( source.fmlHandle, objectHandle, i ) match
            {
                case FML_INVALID_HANDLE => null
                case handle => extractOrder( source, handle, ensembleCount )
            }
        }
        
        val dense = new DenseDataDescription( valueType, denseOrders, denseIndexes )
        val dataHandle = Fieldml_GetDataSource( source.fmlHandle, objectHandle )
        
        val reader = Fieldml_OpenReader( source.fmlHandle, dataHandle )
        if( reader == FML_INVALID_HANDLE )
        {
            throw new FmlException( "Cannot create semidense reader: " + Fieldml_GetLastError( source.fmlHandle )  )
        }
        
        if( valueType.isInstanceOf[EnsembleType] )
        {
            val generator : ( ValueType, Int* ) => Value = ( a, b ) => Value.apply( a, b.map( _.toDouble ):_* ) 
            initializeDenseValues( source, reader, dense, Fieldml_ReadIntSlab, generator )
        }
        else if( valueType.isInstanceOf[ContinuousType] )
        {
            val generator : ( ValueType, Double* ) => Value = Value.apply
            initializeDenseValues( source, reader, dense, Fieldml_ReadDoubleSlab, generator )
        }
        else
        {
            throw new FmlException( "Cannot yet initialize " + valueType.name + " valued parameter evaluator" )
        }
        
        Fieldml_CloseReader( source.fmlHandle, reader )
        
        dense
    }
    
    
    def extract( source : Deserializer, objectHandle : Int ) : ParameterEvaluator =
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )

        val typeHandle = Fieldml_GetValueType( source.fmlHandle, objectHandle )
        val valueType = source.getType( typeHandle )

        val dataDescription = Fieldml_GetParameterDataDescription( source.fmlHandle, objectHandle ) match
        {
            case DataDescriptionType.DESCRIPTION_DENSE_ARRAY => extractDense( source, objectHandle, valueType )
            case DataDescriptionType.DESCRIPTION_DOK_ARRAY => extractDok( source, objectHandle, valueType )
            case d => throw new FmlException( "Unsupported data description: " + d ) 
        }
        
        val dataObjectHandle = Fieldml_GetDataSource( source.fmlHandle, objectHandle )
        val dataObject = source.getDataSource( dataObjectHandle )
        
        val dataStore = new DataStore( dataObject, dataDescription ) 

        val parameterEval = new ParameterEvaluatorValueSource( name, valueType, dataStore )
        
        parameterEval
    }
}
