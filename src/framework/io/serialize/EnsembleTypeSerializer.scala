package framework.io.serialize

import fieldml.valueType.EnsembleType
import fieldml.valueType.bounds.EnsembleBounds
import fieldml.valueType.bounds.ContiguousEnsembleBounds

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.DomainBoundsType
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

class EnsembleTypeSerializer( val valueType : EnsembleType )
{
    def insert( handle : Long ) : Unit =
    {
        val objectHandle = Fieldml_CreateEnsembleDomain( handle, valueType.name, FML_INVALID_HANDLE )
        
        valueType.bounds match
        {
            case c : ContiguousEnsembleBounds => Fieldml_SetContiguousBoundsCount( handle, objectHandle, c.count )
            case unknown => println( "Cannot yet serialize EnsembleBounds " + unknown ) 
        }
    }
}


object EnsembleTypeSerializer
{
    def extract( fmlHandle : Long, objectHandle : Int, region : UserRegion ) :
        Option[EnsembleType] = 
    {
        var ensembleType : EnsembleType = null
        
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
            case 1 => ensembleType = region.createEnsembleType( name, bounds, true )
            case 0 => ensembleType = region.createEnsembleType( name, bounds, false )
            case err => println( "Fieldml_IsEnsembleComponentType failure: " + err )
        }
        
        return Some( ensembleType )
    }
}
