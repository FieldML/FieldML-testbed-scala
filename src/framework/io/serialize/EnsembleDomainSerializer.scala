package framework.io.serialize

import fieldml.domain.EnsembleDomain
import fieldml.domain.bounds.EnsembleBounds
import fieldml.domain.bounds.ContiguousEnsembleBounds

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.DomainBoundsType
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

class EnsembleDomainSerializer( val domain : EnsembleDomain )
{
    def insert( handle : Long ) : Unit =
    {
        val objectHandle = Fieldml_CreateEnsembleDomain( handle, domain.name, FML_INVALID_HANDLE )
        
        domain.bounds match
        {
            case c : ContiguousEnsembleBounds => Fieldml_SetContiguousBoundsCount( handle, objectHandle, c.count )
            case unknown => println( "Cannot yet serialize EnsembleBounds " + unknown ) 
        }
    }
}


object EnsembleDomainSerializer
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
            case DomainBoundsType.BOUNDS_DISCRETE_CONTIGUOUS => bounds = new ContiguousEnsembleBounds( Fieldml_GetContiguousBoundsCount( fmlHandle, objectHandle ) )
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
}
