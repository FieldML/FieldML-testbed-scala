package framework.region

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import framework.io.serialize._

import fieldml._
import fieldml.valueType._
import fieldml.valueType.bounds._
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

class UserRegion( name : String )
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
    
    
    def createEnsembleType( objectName : String, bounds : Int, isComponentEnsemble : Boolean ) : EnsembleType =
    {
        createEnsembleType( objectName, new ContiguousEnsembleBounds( bounds ), isComponentEnsemble )
    }
    
    
    def createEnsembleType( objectName : String, bounds : EnsembleBounds, isComponentEnsemble : Boolean ) : EnsembleType =
    {
        val valueType = new EnsembleType( objectName, bounds, isComponentEnsemble )

        put( valueType )

        valueType
    }

    
    def createContinuousType( objectName : String, components : EnsembleType ) : ContinuousType =
    {
        val valueType = new ContinuousType( objectName, components )

        put( valueType )

        valueType
    }

    
    def createMeshType( objectName : String, bounds : Int, xiComponents : EnsembleType ) : MeshType =
    {
        createMeshType( objectName, new ContiguousEnsembleBounds( bounds ), xiComponents )
    }


    def createMeshType( objectName : String, bounds : EnsembleBounds, xiComponents : EnsembleType ) : MeshType =
    {
        val valueType = new MeshType( objectName, bounds, xiComponents )

        put( valueType )

        valueType
    }
    
    
    def createFunctionEvaluator( name : String, function : ( Array[Double], Array[Double] ) => Array[Double], var1 : AbstractEvaluator, var2 : AbstractEvaluator, valueType : ContinuousType ) : Evaluator =
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
    
    
    def createParameterEvaluator( name : String, valueType : ValueType, location : DataLocation, description : DataDescription ) : ParameterEvaluator =
    {
        val store = new DataStore( location, description )
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
    
    
    def createAbstractEvaluator( name : String, valueType : ValueType ) : AbstractEvaluator =
    {
        val evaluator = new AbstractEvaluatorValueSource( name, valueType )
        
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
        
        for( o <- objectList )
        {
            o match
            {
            case d : EnsembleType => d.insert( handle, d )
            case d : ContinuousType => d.insert( handle, d )
            case d : MeshType => d.insert( handle, d )
            case e : AbstractEvaluator => e.insert( handle, e )
            case e : PiecewiseEvaluator => e.insert( handle, e )
            case e : ParameterEvaluator => e.insert( handle, e )
            case e : ReferenceEvaluator => e.insert( handle, e )
            case e : AggregateEvaluator => e.insert( handle, e )
            case unknown => println( "Cannot yet serialize " + unknown )
            }
        }
        
        Fieldml_WriteFile( handle, "test.xml" )
        Fieldml_Destroy( handle )
    }
}


object UserRegion
{
    val library = loadLibrary()
    
    
    private def getTypeHandles( fmlHandle : Long, handleType : FieldmlHandleType ) : Seq[Int] =
    {
        return for( index <- 1 until Fieldml_GetObjectCount( fmlHandle, handleType ) + 1 )
            yield Fieldml_GetObject( fmlHandle, handleType, index )
    }

    
    private def loadLibrary() : Region =
    {
        val lib = new UserRegion( "library" )
        
        //TODO Icky. Currently, a 'blank' region has the libray auto-imported, so the library can be deduced by interrogating it.
        val fmlHandle = Fieldml_Create( "", "" )
        
        importObjects( fmlHandle, lib )
        
        Fieldml_Destroy( fmlHandle )
        
        lib
    }
    
    
    private def importObjects( fmlHandle : Long, region : UserRegion ) : Unit =
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
            }
        }
    }
    
    
    def fromFile( name : String, filename : String ) : Region =
    {
        val region = new UserRegion( name )
        
        val fmlHandle = Fieldml_CreateFromFile( filename )
        
        importObjects( fmlHandle, region )
        
        Fieldml_Destroy( fmlHandle )
        
        region
    }
}
