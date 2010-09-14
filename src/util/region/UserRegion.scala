package util.region

import fieldml._
import fieldml.domain._
import fieldml.domain.bounds._
import fieldml.evaluator._
import fieldml.evaluator.datastore._

import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.FieldmlApi._

import framework.valuesource._
import framework.value._
import framework._

import util._
import util.library._
import util.exception._

class UserRegion( name : String )
    extends Region( name )
{
    private def put( obj : FieldmlObject ) : Unit =
    {
        objects.get( obj.name ) match
        {
            case None => objects.put( obj.name, obj )
            case s: Some[_] => throw new FmlObjectCollisionException( s.get, obj )
        }
    }
    
    
    def createEnsembleDomain( objectName : String, bounds : Int, isComponentEnsemble : Boolean ) : EnsembleDomain =
    {
        return createEnsembleDomain( objectName, new ContiguousEnsembleBounds( bounds ), isComponentEnsemble )
    }
    
    
    def createEnsembleDomain( objectName : String, bounds : EnsembleBounds, isComponentEnsemble : Boolean ) : EnsembleDomain =
    {
        val domain = new EnsembleDomain( objectName, bounds, isComponentEnsemble )

        put( domain )

        return domain
    }

    
    def createContinuousDomain( objectName : String, components : EnsembleDomain ) : ContinuousDomain =
    {
        val domain = new ContinuousDomain( objectName, components )

        put( domain )

        return domain
    }

    
    def createFunctionEvaluator( name : String, function : ( Array[Double], Array[Double] ) => Array[Double], domain1 : ContinuousDomain, domain2 : ContinuousDomain, valueDomain : ContinuousDomain ) : Evaluator =
    {
        val evaluator = new FunctionEvaluator( name, function, domain1, domain2, valueDomain ) 

        put( evaluator )
        
        context.add( new FunctionEvaluatorValueSource( evaluator ) )
        
        return evaluator
    }
    
    
    def createReferenceEvaluator( name : String, refEvaluatorName : String, refRegion : Region, valueDomain : Domain ) : ReferenceEvaluator =
    {
        val refEvaluator : Evaluator = refRegion.getObject( refEvaluatorName )
        val evaluator = new ReferenceEvaluator( name, valueDomain, refEvaluator )
        
        put( evaluator )
        
        context.add( new ReferenceEvaluatorValueSource( evaluator, refRegion.context ) )
        
        return evaluator
    }
    
    
    def createPiecewiseEvaluator( name : String, index : EnsembleDomain, valueDomain : Domain ) : PiecewiseEvaluator =
    {
        val evaluator = new PiecewiseEvaluator( name, valueDomain, index )
        
        put( evaluator )
        
        context.add( new PiecewiseEvaluatorValueSource( evaluator ) )
        
        return evaluator
    }
    
    
    def createParameterEvaluator( name : String, valueDomain : Domain, location : DataLocation, description : DataDescription ) : ParameterEvaluator =
    {
        val evaluator = new ParameterEvaluator( name, valueDomain, location, description )
        
        put( evaluator )
        
        context.add( new ParameterEvaluatorValueSource( evaluator ) )
        
        return evaluator
    }
    
    
    def set( domain : EnsembleDomain, value : Int )
    {
        context( domain ) = new EnsembleValue( value )
    }
    
    
    def set( domain : ContinuousDomain, values : Double* )
    {
        context( domain ) = new ContinuousValue( values.toArray )
    }
    
    
    def serialize() : Unit =
    {
        val handle = Fieldml_Create( "", "test" )
        
        for( o <- objects.values )
        {
            o match
            {
            case d : EnsembleDomain => EnsembleDomain.insert( handle, d )
            case d : ContinuousDomain => ContinuousDomain.insert( handle, d )
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
                case FHT_ENSEMBLE_DOMAIN => EnsembleDomain.extract( fmlHandle, objectHandle, lib )
                case FHT_CONTINUOUS_DOMAIN => ContinuousDomain.extract( fmlHandle, objectHandle, lib )
                case FHT_REMOTE_CONTINUOUS_EVALUATOR => RemoteEvaluatorGenerator.generateContinuousEvaluator( fmlHandle, objectHandle, lib )
                case _ => println( "Extracting object type " + fmlType + " not yet supported" )
            }
        }
        
        Fieldml_Destroy( fmlHandle )
        
        return lib
    }
}
