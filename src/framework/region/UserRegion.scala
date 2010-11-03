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
        return createEnsembleType( objectName, new ContiguousEnsembleBounds( bounds ), isComponentEnsemble )
    }
    
    
    def createEnsembleType( objectName : String, bounds : EnsembleBounds, isComponentEnsemble : Boolean ) : EnsembleType =
    {
        val valueType = new EnsembleType( objectName, bounds, isComponentEnsemble )

        put( valueType )

        return valueType
    }

    
    def createContinuousType( objectName : String, components : EnsembleType ) : ContinuousType =
    {
        val valueType = new ContinuousType( objectName, components )

        put( valueType )

        return valueType
    }

    
    def createMeshType( objectName : String, bounds : Int, xiComponents : EnsembleType ) : MeshType =
    {
        return createMeshType( objectName, new ContiguousEnsembleBounds( bounds ), xiComponents )
    }


    def createMeshType( objectName : String, bounds : EnsembleBounds, xiComponents : EnsembleType ) : MeshType =
    {
        val valueType = new MeshType( objectName, bounds, xiComponents )

        put( valueType )

        return valueType
    }
    
    
    def createFunctionEvaluator( name : String, function : ( Array[Double], Array[Double] ) => Array[Double], var1 : AbstractEvaluator, var2 : AbstractEvaluator, valueType : ContinuousType ) : Evaluator =
    {
        val evaluator = new FunctionEvaluatorValueSource( name, function, var1, var2, valueType ) 

        put( evaluator )
        
        return evaluator
    }
    
    
    def createReferenceEvaluator( name : String, refEvaluatorName : String, refRegion : Region, valueType : ValueType ) : ReferenceEvaluator =
    {
        val refEvaluator : Evaluator = refRegion.getObject( refEvaluatorName )
        val evaluator = new ReferenceEvaluatorValueSource( name, valueType, refEvaluator )
        
        put( evaluator )
        
        return evaluator
    }
    
    
    def createPiecewiseEvaluator( name : String, index : Evaluator, valueType : ValueType ) : PiecewiseEvaluator =
    {
        val evaluator = new PiecewiseEvaluatorValueSource( name, valueType, index )
        
        put( evaluator )
        
        return evaluator
    }
    
    
    def createParameterEvaluator( name : String, valueType : ValueType, location : DataLocation, description : DataDescription ) : ParameterEvaluator =
    {
        val store = new DataStore( location, description )
        val evaluator = new ParameterEvaluatorValueSource( name, valueType, store )
        
        put( evaluator )
        
        return evaluator
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
                case s : Some[ValueType] => s.get
                case None => throw new FmlInvalidObjectException( "Type " + baseEvaluator.valueType + " for evaluator " + baseEvaluator + " does not have a subtype called " + subname )
            }
            case _ => throw new FmlInvalidObjectException( "Evaluator " + baseEvaluator + " does not have a structured value type" )
        }
        val evaluator = new SubtypeEvaluatorValueSource( baseEvaluator, subtype, subname )
        
        put( evaluator )
        
        return evaluator
    }

    
    def serialize() : Unit =
    {
        val handle = Fieldml_Create( "", "test" )
        
        for( o <- objectList )
        {
            o match
            {
            case d : EnsembleType => d.insert( handle )
            case d : ContinuousType => d.insert( handle )
            case d : MeshType => d.insert( handle )
            case e : PiecewiseEvaluator => e.insert( handle )
            case e : ParameterEvaluator => e.insert( handle )
            case e : ReferenceEvaluator => e.insert( handle )
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
        
        val fmlHandle = Fieldml_Create( "", "" )
        
        for(
            fmlType <- FieldmlHandleType.values;
            objectHandle <- getTypeHandles( fmlHandle, fmlType )
            )
        {
            fmlType match
            {
                case FHT_ENSEMBLE_DOMAIN => EnsembleTypeSerializer.extract( fmlHandle, objectHandle, lib )
                case FHT_CONTINUOUS_DOMAIN => ContinuousTypeSerializer.extract( fmlHandle, objectHandle, lib )
                case FHT_REMOTE_CONTINUOUS_EVALUATOR => RemoteEvaluatorGenerator.generateContinuousEvaluator( fmlHandle, objectHandle, lib )
                case _ => println( "Extracting object type " + fmlType + " not yet supported" )
            }
        }
        
        Fieldml_Destroy( fmlHandle )
        
        return lib
    }
}
