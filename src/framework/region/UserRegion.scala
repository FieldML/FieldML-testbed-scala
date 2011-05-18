package framework.region

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import framework.io.serialize._

import fieldml._
import fieldml.valueType._
import fieldml.evaluator._

import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

import framework.datastore._
import framework.valuesource._
import framework.value._
import framework.io
import framework._

import util._
import util.library._
import util.exception._

class UserRegion private( name : String, val imports : Array[Pair[String, String]] )
    extends Region( name )
{
    //Used for serialization, which must be order-sensitive.
    private val objectList = ArrayBuffer[FieldmlObject]()
    
    private def put( obj : FieldmlObject ) : Unit =
    {
        objects.get( obj.name ) match
        {
            case None => objects.put( obj.name, obj )
            case s: Some[_] => throw new FmlObjectCollisionException( s.get, obj )
        }
        
        objectList.append( obj )
    }
    
    
    def createEnsembleType( objectName : String, max : Int, isComponentEnsemble : Boolean ) : EnsembleType =
    {
        val ensemble = createEnsembleType( objectName, isComponentEnsemble )
        
        ensemble.elementSet.add( 1, max, 1 )
        
        ensemble
    }
    
    
    def createEnsembleType( objectName : String, isComponentEnsemble : Boolean ) : EnsembleType =
    {
        val valueType = new EnsembleType( objectName, isComponentEnsemble )

        put( valueType )

        valueType
    }

    
    def createContinuousType( objectName : String, components : EnsembleType ) : ContinuousType =
    {
        val valueType = new ContinuousType( objectName, components )

        put( valueType )

        valueType
    }

    
    def createMeshType( objectName : String, elementCount : Int, dimensions : Int ) : MeshType =
    {
        val valueType = new MeshType( objectName, elementCount, dimensions )

        put( valueType )

        valueType
    }
    
    
    def createTextFileResource( objectName : String, href : String ) : DataResource =
    {
        val dataResource = new TextFileDataResource( objectName )
        dataResource.href = href
        
        put( dataResource )
        
        dataResource
    }
    
    
    def createTextInlineResource( objectName : String, data : String ) : DataResource =
    {
        val dataResource = new InlineDataResource( objectName, data )
        
        put( dataResource )
        
        dataResource
    }
    
    
    def createTextDataSource( objectName : String, resource : DataResource, firstLine : Int, count : Int, length : Int, head : Int, tail : Int ) : DataSource =
    {
        val dataSource = new TextDataSource( objectName, resource, firstLine, count, length, head, tail )
        
        put( dataSource )
        
        dataSource
    }
    
    
    def createFunctionEvaluator( name : String, function : ( Array[Double], Array[Double] ) => Array[Double], var1 : ArgumentEvaluator, var2 : ArgumentEvaluator, valueType : ContinuousType ) : Evaluator =
    {
        val evaluator = new FunctionEvaluatorValueSource( name, function, var1, var2, valueType ) 

        put( evaluator )
        
        evaluator
    }
    
    
    def createReferenceEvaluator( name : String, refEvaluatorName : String, refRegion : Region ) : ReferenceEvaluator =
    {
        val refEvaluator : Evaluator = refRegion.getObject( refEvaluatorName )
        val evaluator = new ReferenceEvaluatorValueSource( name, refEvaluator )
        
        put( evaluator )
        
        evaluator
    }
    
    
    def createPiecewiseEvaluator( name : String, index : Evaluator, valueType : ValueType ) : PiecewiseEvaluator =
    {
        val evaluator = new PiecewiseEvaluatorValueSource( name, valueType, index )
        
        put( evaluator )
        
        evaluator
    }
    
    
    def createAggregateEvaluator( name : String, valueType : ContinuousType ) : AggregateEvaluator =
    {
        val evaluator = new AggregateEvaluatorValueSource( name, valueType )
        
        put( evaluator )
        
        evaluator
    }
    
    
    def createParameterEvaluator( name : String, valueType : ValueType, data : DataSource, description : DataDescription ) : ParameterEvaluator =
    {
        val store = new DataStore( data, description )
        val evaluator = new ParameterEvaluatorValueSource( name, valueType, store )
        
        put( evaluator )
        
        evaluator
    }
    
    
    def createElementSet( name : String, valueType : ValueType, elements : Int* ) : ElementSet =
    {
        val set = new ElementSet( name, valueType, elements:_* )
        
        put( set )
        
        set
    }
    
    
    def createArgumentEvaluator( name : String, valueType : ValueType ) : ArgumentEvaluator =
    {
        val evaluator = new ArgumentEvaluatorValueSource( name, valueType )
        
        put( evaluator )
        
        evaluator
    }
    
    
    def createSubtypeEvaluator( baseEvaluator : Evaluator, subname : String ) : SubtypeEvaluator =
    {
        val subtype = baseEvaluator.valueType match
        {
            case s : StructuredType => s.subtype( subname ) match
            {
                case null => throw new FmlInvalidObjectException( "Type " + baseEvaluator.valueType + " for evaluator " + baseEvaluator + " does not have a subtype called " + subname )
                case s => s 
            }
            case _ => throw new FmlInvalidObjectException( "Evaluator " + baseEvaluator + " does not have a structured value type" )
        }
        val evaluator = new SubtypeEvaluatorValueSource( baseEvaluator, subname )
        
        put( evaluator )
        
        evaluator
    }

    
    def serialize() : Unit =
    {
        val handle = Fieldml_Create( "", "test" )
        
        val importIdx = Fieldml_AddImportSource( handle, "library_0.3.xml", "library" )
        for( p <- imports )
        {
            Fieldml_AddImport( handle, importIdx, p._1, p._2 )
        }
        
        objectList.filter( _.isLocal ).foreach( _ match
        {
        case d : DataResource => d.insert( handle, d )
        case d : EnsembleType => d.insert( handle, d )
        case d : ContinuousType => d.insert( handle, d )
        case d : MeshType => d.insert( handle, d )
        case e : ArgumentEvaluator => e.insert( handle, e )
        case e : PiecewiseEvaluator => e.insert( handle, e )
        case e : ParameterEvaluator => e.insert( handle, e )
        case e : ReferenceEvaluator => e.insert( handle, e )
        case e : AggregateEvaluator => e.insert( handle, e )
        case unknown => println( "Cannot yet serialize " + unknown )
        }
        )
        
        Fieldml_WriteFile( handle, "test.xml" )
        Fieldml_Destroy( handle )
    }
}


object UserRegion
{
    private def getTypeHandles( fmlHandle : Int, handleType : FieldmlHandleType ) : Seq[Int] =
    {
        val locals = for( index <- 1 to Fieldml_GetObjectCount( fmlHandle, handleType ) ; objectHandle = Fieldml_GetObject( fmlHandle, handleType, index ) if Fieldml_IsObjectLocal( fmlHandle, objectHandle ) == 1 )
            yield objectHandle
            
        val imports = for(
            importIndex <- 1 to Fieldml_GetImportSourceCount( fmlHandle ) ;
            index <- 1 to Fieldml_GetImportCount( fmlHandle, importIndex ) ;
            objectHandle = Fieldml_GetImportObject( fmlHandle, importIndex, index )
            if( Fieldml_GetObjectType( fmlHandle, objectHandle ) == handleType ) )
            yield objectHandle
            
        return imports ++ locals
    }

    
    private def importObjects( fmlHandle : Int, region : UserRegion ) : Unit =
    {
        val source = new Deserializer( fmlHandle )
        for(
            fmlType <- FieldmlHandleType.values;
            objectHandle <- getTypeHandles( fmlHandle, fmlType )
            )
        {
            val obj = source.get( objectHandle )
            //NOTE This check is only needed because RemoteEvaluatorGenerator returns null for unsupported evaluators.  
            if( obj != null )
            {
                region.put( obj )
                if( Fieldml_IsObjectLocal( fmlHandle, objectHandle ) != 1 )
                {
                    obj.isLocal = false
                }
            }
        }
    }
    
    
    def fromFile( name : String, filename : String ) : Region =
    {
        val fmlHandle = Fieldml_CreateFromFile( filename )
        
        val count = Fieldml_GetErrorCount( fmlHandle )
        for( i <- 1 to count )
            println( Fieldml_GetError( fmlHandle, i ) )
        
        //So very, very dirty.
        val builder1 = new java.lang.StringBuilder( 100 )
        val builder2 = new java.lang.StringBuilder( 100 )
        val imports = for(
            importSourceIdx <- 1 to Fieldml_GetImportSourceCount( fmlHandle );
            importIdx <- 1 to Fieldml_GetImportCount( fmlHandle, importSourceIdx );
            l1 = Fieldml_CopyImportLocalName( fmlHandle, importSourceIdx, importIdx, builder1, 100 );
            l2 = Fieldml_CopyImportLocalName( fmlHandle, importSourceIdx, importIdx, builder2, 100 );
            s1 = builder1.toString;
            s2 = builder2.toString )
            yield Pair( s1, s2 )
        
        val region = new UserRegion( name, imports.toArray )
        
        importObjects( fmlHandle, region )
        
        Fieldml_Destroy( fmlHandle )
        
        region
    }
    
    
    def fromScratch( name : String, imports : Pair[String, String]* ) : UserRegion =
    {
        val region = new UserRegion( name, imports.toArray[Pair[String, String]] )
        val fmlHandle = Fieldml_Create( "", name )
        
        val importId = Fieldml_AddImportSource( fmlHandle, "library.xml", "library" )
        
        for( p <- imports )
        {
            Fieldml_AddImport( fmlHandle, importId, p._1, p._2 )
        }
        
        importObjects( fmlHandle, region )
        
        Fieldml_Destroy( fmlHandle )
        
        region
    }
}
