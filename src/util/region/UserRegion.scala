package util.region

import fieldml.jni.FieldmlApi._

import fieldml._
import fieldml.domain._
import fieldml.domain.bounds._

import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.FieldmlApi._

import util._
import util.library._
import util.exception._

class UserRegion( name : String )
    extends Region( name, UserRegion.library )
{
    private def put( obj : FieldmlObject ) =
    {
        objects.get( obj.name ) match
        {
            case None => objects.put( obj.name, obj )
            case s: Some[_] => throw new FmlObjectCollisionException( s.get, obj )
        }
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


private object UserRegion
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