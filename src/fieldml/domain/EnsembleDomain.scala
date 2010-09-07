package fieldml.domain

import fieldml.FieldmlObject

import fieldml.domain.bounds._
import util.region._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.DomainBoundsType._

import util.exception._

class EnsembleDomain( name : String, val bounds : EnsembleBounds, val isComponent : Boolean )
    extends Domain( name )
{
}


object EnsembleDomain
{
    def extract( fmlHandle : Long, objectHandle : Int, region : UserRegion ) :
        Option[EnsembleDomain] = 
    {
        var ensembleDomain : EnsembleDomain = null
        
        val name = Fieldml_GetObjectName( fmlHandle, objectHandle )
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        if( objectType != FHT_ENSEMBLE_DOMAIN )
        {
            Fieldml_GetLastError( fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( objectHandle )
                case _ => throw new FmlTypeException( name, objectType, FHT_ENSEMBLE_DOMAIN )
            }
        }
        
        var bounds : EnsembleBounds = null
        
        Fieldml_GetDomainBoundsType( fmlHandle, objectHandle ) match
        {
            case BOUNDS_DISCRETE_CONTIGUOUS => bounds = new ContiguousEnsembleBounds( Fieldml_GetContiguousBoundsCount( fmlHandle, objectHandle ) )
            case unknown => println( "Cannot yet extract bounds type " + unknown )
        }
        
        Fieldml_IsEnsembleComponentDomain( fmlHandle, objectHandle ) match
        {
            case 1 => ensembleDomain = region.createEnsembleDomain( name, bounds, true )
            case 0 => ensembleDomain = region.createEnsembleDomain( name, bounds, false )
            case err => println( "Fieldml_IsEnsembleComponentDomain failure: " + err )
        }
        
        return Some( ensembleDomain )
    }


    def insert( handle : Long, d : EnsembleDomain ) : Unit =
    {
        var objectHandle = Fieldml_CreateEnsembleDomain( handle, d.name, FML_INVALID_HANDLE )
        
        d.bounds match
        {
            case c : ContiguousEnsembleBounds => Fieldml_SetContiguousBoundsCount( handle, objectHandle, c.count )
            case unknown => println( "Cannot yet serialize EnsembleBounds " + unknown ) 
        }
    }
}