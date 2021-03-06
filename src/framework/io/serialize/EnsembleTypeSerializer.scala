package framework.io.serialize

import scala.collection.mutable.BitSet

import fieldml.valueType.EnsembleType

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlEnsembleMembersType._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

object EnsembleTypeSerializer
{
    def insertElements( handle : Int, objectHandle : Int, valueType : EnsembleType ) : Unit =
    {
        val elementArray = valueType.elementSet.toArray
        
        //NOTE elementArray is a bitset array, and is therefore already sorted and without duplicates.
        val min = elementArray.first
        val max = elementArray.last
        var stride = 1
        if( elementArray.size > 1 )
        {
            stride = elementArray( 1 ) - min
        }
        
        if( elementArray.size == ( max - min ) + 1 )
        {
            //Trivial case. n elements with a stride of 1.
            Fieldml_SetEnsembleMembersRange( handle, objectHandle, min, max, stride )
            return
        }
        
        val strides = elementArray.foldLeft( Pair( new BitSet, min ) )( ( t, i ) => Pair( t._1 + ( i - t._2 ),  i ) )._1
        
        //TODO Handle non-trivial element sets.
    }

    
    def insert( handle : Int, valueType : EnsembleType ) : Unit =
    {
        if( !valueType.isComponent )
        {
            val objectHandle = Fieldml_CreateEnsembleType( handle, valueType.name )

            insertElements( handle, objectHandle, valueType )
        }
    }
    
    
    def extractElements( source : Deserializer, objectHandle : Int, ensemble : EnsembleType ) : Unit =
    {
        val entries = new Array[Int]( 3 )

        val count = Fieldml_GetMemberCount( source.fmlHandle, objectHandle )
        
        val membersType = Fieldml_GetEnsembleMembersType( source.fmlHandle, objectHandle )
        
        if( membersType == FML_ENSEMBLE_MEMBER_RANGE )
        {
            val min = Fieldml_GetEnsembleMembersMin( source.fmlHandle, objectHandle )
            val max = Fieldml_GetEnsembleMembersMax( source.fmlHandle, objectHandle )
            val stride = Fieldml_GetEnsembleMembersStride( source.fmlHandle, objectHandle )
            
            ensemble.elementSet.add( min, max, stride )
        }
        else
        {
            val data = Fieldml_GetDataSource( source.fmlHandle, objectHandle )
            
            val streamHandle = Fieldml_OpenReader( source.fmlHandle, data )
            
            val count = membersType match
            {
                case FML_ENSEMBLE_MEMBER_LIST_DATA => 1
                case FML_ENSEMBLE_MEMBER_RANGE_DATA => 2
                case FML_ENSEMBLE_MEMBER_STRIDE_RANGE_DATA => 3
            }
            val offsets = new Array[Int]( 1 );
            val sizes = new Array[Int]( 1 );
            
            offsets( 0 ) = 0
            sizes( 0 ) = count
            var result = Fieldml_ReadIntSlab( streamHandle, offsets, sizes, entries )
            
            while( result == count )
            {
                membersType match
                {
                    case FML_ENSEMBLE_MEMBER_LIST_DATA => ensemble.elementSet.add( entries( 0 ) )
                    case FML_ENSEMBLE_MEMBER_RANGE_DATA => ensemble.elementSet.add( entries( 0 ), entries( 1 ), 1 )
                    case FML_ENSEMBLE_MEMBER_STRIDE_RANGE_DATA => ensemble.elementSet.add( entries( 0 ), entries( 1 ), entries( 2 ) )
                }
                
                offsets( 0 ) += count
                
                result = Fieldml_ReadIntSlab( streamHandle, offsets, sizes, entries )
            }
            
            Fieldml_CloseReader( streamHandle )
        }
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
        
        extractElements( source, objectHandle, ensemble )
        
        ensemble
    }
}
