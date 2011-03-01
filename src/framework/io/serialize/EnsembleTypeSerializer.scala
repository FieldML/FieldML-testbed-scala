package framework.io.serialize

import fieldml.valueType.EnsembleType
import fieldml.valueType.bounds.EnsembleBounds
import fieldml.valueType.bounds.ContiguousEnsembleBounds

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.TypeBoundsType
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

object EnsembleTypeSerializer
{
    def insert( handle : Long, valueType : EnsembleType ) : Unit =
    {
        val objectHandle = Fieldml_CreateEnsembleType( handle, valueType.name, valueType.isComponent match { case true => 1; case false => 0} )
        
        valueType.bounds match
        {
            case c : ContiguousEnsembleBounds => Fieldml_SetContiguousBoundsCount( handle, objectHandle, c.count )
            case unknown => println( "Cannot yet serialize EnsembleBounds " + unknown ) 
        }
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : EnsembleType = 
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val bounds = Fieldml_GetBoundsType( source.fmlHandle, objectHandle ) match
        {
            case TypeBoundsType.BOUNDS_DISCRETE_CONTIGUOUS => new ContiguousEnsembleBounds( Fieldml_GetContiguousBoundsCount( source.fmlHandle, objectHandle ) )
            case unknown => throw new FmlException( "Cannot yet extract bounds type " + unknown + " for " + name )
        }
        
        Fieldml_IsEnsembleComponentType( source.fmlHandle, objectHandle ) match
        {
            case 1 => new EnsembleType( name, bounds, true )
            case 0 => new EnsembleType( name, bounds, false )
            case err => throw new FmlException( "Fieldml_IsEnsembleComponentType failure: " + err )
        }
    }
}
