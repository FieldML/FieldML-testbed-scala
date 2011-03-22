package framework.io.serialize

import fieldml.valueType.EnsembleType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

object EnsembleTypeSerializer
{
    def insert( handle : Int, valueType : EnsembleType ) : Unit =
    {
        val objectHandle = Fieldml_CreateEnsembleType( handle, valueType.name, valueType.isComponent match { case true => 1; case false => 0} )
        
        val elementArray = valueType.elementSet.toArray
        
        Fieldml_AddEnsembleElements( handle, objectHandle, elementArray, elementArray.size ) 
    }

    
    def extract( source : Deserializer, objectHandle : Int ) : EnsembleType = 
    {
        val name = Fieldml_GetObjectName( source.fmlHandle, objectHandle )
        
        val ensemble = Fieldml_IsEnsembleComponentType( source.fmlHandle, objectHandle ) match
        {
            case 1 => new EnsembleType( name, true )
            case 0 => new EnsembleType( name, false )
            case err => throw new FmlException( "Fieldml_IsEnsembleComponentType failure: " + err )
        }
        
        val count = Fieldml_GetElementCount( source.fmlHandle, objectHandle )
        
        val values = new Array[Int]( count )
        Fieldml_GetElementEntries( source.fmlHandle, objectHandle, 1, values, count )
        
        for( i <- values )
        {
            ensemble.elementSet.add( i )
        }
        
        ensemble
    }
}
