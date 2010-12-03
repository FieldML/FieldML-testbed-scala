package framework.io.serialize

import scala.collection.mutable.Map

import fieldml.evaluator._
import fieldml.valueType._
import fieldml.FieldmlObject

import fieldml.jni.FieldmlApi._
import fieldml.jni.TypeBoundsType
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion

import util.library.RemoteEvaluatorGenerator
import util.exception._

class Deserializer( val fmlHandle : Long )
{
    private val objects = Map[Int, FieldmlObject]()
    
    
    def get( objectHandle : Int ) : FieldmlObject =
    {
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        objectType match
        {
            case FHT_ENSEMBLE_TYPE => getEnsembleType( objectHandle )
            case FHT_CONTINUOUS_TYPE => getContinuousType( objectHandle )
            case FHT_MESH_TYPE => getMeshType( objectHandle )
            case FHT_REMOTE_EVALUATOR => getRemoteEvaluator( objectHandle )
            case FHT_ABSTRACT_EVALUATOR => getAbstractEvaluator( objectHandle )
            case FHT_REFERENCE_EVALUATOR => getReferenceEvaluator( objectHandle )
            case _ => throw new FmlException( "Extracting object type " + objectType + " not yet supported" );
        }
    }
    
    
    def getEvaluator( objectHandle : Int ) : Evaluator =
    {
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        objectType match
        {
            case FHT_REFERENCE_EVALUATOR => return getReferenceEvaluator( objectHandle )
            case FHT_PARAMETER_EVALUATOR => return getParameterEvaluator( objectHandle )
            case FHT_ABSTRACT_EVALUATOR => return getAbstractEvaluator( objectHandle )
            case FHT_AGGREGATE_EVALUATOR => return getAggregateEvaluator( objectHandle )
            case FHT_PIECEWISE_EVALUATOR => return getPiecewiseEvaluator( objectHandle )
            case FHT_REMOTE_EVALUATOR => getRemoteEvaluator( objectHandle )
            case FHT_UNKNOWN => throw new FmlUnknownObjectException( objectHandle )
            case _ => throw new FmlInvalidObjectException( Fieldml_GetObjectName( fmlHandle, objectHandle ) + " is not a known evaluator" )
        }
    }
    
    
    def getType( objectHandle : Int ) : ValueType =
    {
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        objectType match
        {
            case FHT_ENSEMBLE_TYPE => return getEnsembleType( objectHandle )
            case FHT_CONTINUOUS_TYPE => return getContinuousType( objectHandle )
            case FHT_MESH_TYPE => return getMeshType( objectHandle )
            case FHT_UNKNOWN => throw new FmlUnknownObjectException( objectHandle )
            case _ => throw new FmlInvalidObjectException( Fieldml_GetObjectName( fmlHandle, objectHandle ) + " is not a known value type" )
        }
    }
    
    
    def getEnsembleType( objectHandle : Int ) : EnsembleType =
    {
        getTypedObject( objectHandle, FHT_ENSEMBLE_TYPE, classOf[EnsembleType] ) match
        {
            case s : Some[EnsembleType] => s.get
            case None => objects( objectHandle ) = EnsembleTypeSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[EnsembleType]
        }
    }
    
    
    def getContinuousType( objectHandle : Int ) : ContinuousType =
    {
        getTypedObject( objectHandle, FHT_CONTINUOUS_TYPE, classOf[ContinuousType] ) match
        {
            case s : Some[ContinuousType] => s.get
            case None => objects( objectHandle ) = ContinuousTypeSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[ContinuousType]
        }
    }
    
    
    def getMeshType( objectHandle : Int ) : MeshType =
    {
        getTypedObject( objectHandle, FHT_MESH_TYPE, classOf[MeshType] ) match
        {
            case s : Some[MeshType] => s.get
            case None => objects( objectHandle ) = MeshTypeSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[MeshType]
        }
    }
    
    
    def getRemoteEvaluator( objectHandle : Int ) : Evaluator =
    {
        getTypedObject( objectHandle, FHT_REMOTE_EVALUATOR, classOf[Evaluator] ) match
        {
            case s : Some[Evaluator] => s.get
            case None => objects( objectHandle ) = RemoteEvaluatorGenerator.generateContinuousEvaluator( this, objectHandle ); objects( objectHandle ).asInstanceOf[Evaluator]
        }
    }
    
    
    def getReferenceEvaluator( objectHandle : Int ) : ReferenceEvaluator =
    {
        getTypedObject( objectHandle, FHT_REFERENCE_EVALUATOR, classOf[ReferenceEvaluator] ) match
        {
            case s : Some[ReferenceEvaluator] => s.get
            case None => objects( objectHandle ) = ReferenceEvaluatorSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[ReferenceEvaluator]
        }
    }
    
    
    def getParameterEvaluator( objectHandle : Int ) : ParameterEvaluator =
    {
        getTypedObject( objectHandle, FHT_PARAMETER_EVALUATOR, classOf[ParameterEvaluator] ) match
        {
            case s : Some[ParameterEvaluator] => s.get
            case None => objects( objectHandle ) = ParameterEvaluatorSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[ParameterEvaluator]
        }
    }
    
    
    def getAggregateEvaluator( objectHandle : Int ) : AggregateEvaluator =
    {
        getTypedObject( objectHandle, FHT_AGGREGATE_EVALUATOR, classOf[AggregateEvaluator] ) match
        {
            case s : Some[AggregateEvaluator] => s.get
            case None => objects( objectHandle ) = AggregateEvaluatorSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[AggregateEvaluator]
        }
    }
    
    
    def getPiecewiseEvaluator( objectHandle : Int ) : PiecewiseEvaluator =
    {
        getTypedObject( objectHandle, FHT_PIECEWISE_EVALUATOR, classOf[PiecewiseEvaluator] ) match
        {
            case s : Some[PiecewiseEvaluator] => s.get
            case None => objects( objectHandle ) = PiecewiseEvaluatorSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[PiecewiseEvaluator]
        }
    }
    
    
    def getAbstractEvaluator( objectHandle : Int ) : AbstractEvaluator =
    {
        getTypedObject( objectHandle, FHT_ABSTRACT_EVALUATOR, classOf[AbstractEvaluator] ) match
        {
            case s : Some[AbstractEvaluator] => s.get
            case None => objects( objectHandle ) = AbstractEvaluatorSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[AbstractEvaluator]
        }
    }
    
    
    private def getTypedObject[A <: FieldmlObject]( objectHandle : Int, objectType : FieldmlHandleType, clazz : Class[A] ) : Option[A] =
    {
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        val name = Fieldml_GetObjectName( fmlHandle, objectHandle )
        
        try
        {
            objects.get( objectHandle ) match
            {
                case s : Some[_] =>
                {
                    val existingObj : A = objects( objectHandle ).asInstanceOf[A]
                    return Some( existingObj )
                }
                case None =>
            }
        }
        catch
        {
            case ex : ClassCastException => throw new FmlTypeException( name, objectType, objectType )
        }
        
        if( objectType != objectType )
        {
            Fieldml_GetLastError( fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( objectHandle )
                case _ => throw new FmlTypeException( name, objectType, objectType )
            }
        }
        
        return None
    }
}
