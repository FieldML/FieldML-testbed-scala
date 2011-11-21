package framework.io.serialize

import scala.collection.mutable.Map

import fieldml.evaluator._
import fieldml.valueType._
import fieldml.DataResource
import fieldml.DataSource
import fieldml.FieldmlObject

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlHandleType._
import fieldml.jni.FieldmlHandleType
import fieldml.jni.FieldmlApiConstants._

import framework.region.UserRegion
import framework.valuesource.SubtypeEvaluatorValueSource

import util.library.ExternalEvaluatorGenerator
import util.exception._

class Deserializer( val fmlHandle : Int )
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
            case FHT_BOOLEAN_TYPE => getBooleanType( objectHandle )
            case FHT_DATA_RESOURCE => getDataResource( objectHandle )
            case FHT_DATA_SOURCE => getDataSource( objectHandle )
            case FHT_REFERENCE_EVALUATOR => return getReferenceEvaluator( objectHandle )
            case FHT_PARAMETER_EVALUATOR => return getParameterEvaluator( objectHandle )
            case FHT_ARGUMENT_EVALUATOR => return getArgumentOrSubtypeEvaluator( objectHandle )
            case FHT_AGGREGATE_EVALUATOR => return getAggregateEvaluator( objectHandle )
            case FHT_PIECEWISE_EVALUATOR => return getPiecewiseEvaluator( objectHandle )
            case FHT_EXTERNAL_EVALUATOR => return getExternalEvaluator( objectHandle )
            case FHT_CONSTANT_EVALUATOR => return getConstantEvaluator( objectHandle )
            case _ => throw new FmlException( "Extracting object type " + objectType + " not yet supported" );
        }
    }
    
    
    def getEvaluator( objectHandle : Int ) : Evaluator =
    {
        if( objects.getOrElse( objectHandle, null ).isInstanceOf[Evaluator] )
        {
            return objects( objectHandle ).asInstanceOf[Evaluator]
        }
        
        val objectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        
        objectType match
        {
            case FHT_REFERENCE_EVALUATOR => return getReferenceEvaluator( objectHandle )
            case FHT_PARAMETER_EVALUATOR => return getParameterEvaluator( objectHandle )
            case FHT_ARGUMENT_EVALUATOR => return getArgumentOrSubtypeEvaluator( objectHandle )
            case FHT_AGGREGATE_EVALUATOR => return getAggregateEvaluator( objectHandle )
            case FHT_PIECEWISE_EVALUATOR => return getPiecewiseEvaluator( objectHandle )
            case FHT_EXTERNAL_EVALUATOR => return getExternalEvaluator( objectHandle )
            case FHT_CONSTANT_EVALUATOR => return getConstantEvaluator( objectHandle )
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
    
    
    def getBooleanType( objectHandle : Int ) : BooleanType =
    {
        getTypedObject( objectHandle, FHT_BOOLEAN_TYPE, classOf[BooleanType] ) match
        {
            case s : Some[BooleanType] => s.get
            case None => objects( objectHandle ) = BooleanTypeSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[BooleanType]
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
    
    
    def getDataResource( objectHandle : Int ) : DataResource =
    {
        getTypedObject( objectHandle, FHT_DATA_RESOURCE, classOf[DataResource] ) match
        {
            case s : Some[DataResource] => s.get
            case None => objects( objectHandle ) = DataResourceSerializer.extract( this, objectHandle ); objects( objectHandle).asInstanceOf[DataResource]
        }
    }
    

    def getDataSource( objectHandle : Int ) : DataSource =
    {
        getTypedObject( objectHandle, FHT_DATA_SOURCE, classOf[DataSource] ) match
        {
            case s : Some[DataSource] => s.get
            case None => objects( objectHandle ) = DataSourceSerializer.extract( this, objectHandle ); objects( objectHandle).asInstanceOf[DataSource]
        }
    }
    

    private def getSubtypeEvaluator( objectHandle : Int ) : Option[Evaluator] =
    {
        val name = Fieldml_GetObjectName( fmlHandle, objectHandle )
        if( name == null )
        {
            return None
        }
        
        val lastDot = name.lastIndexOf( '.' )
        if( lastDot == -1 )
        {
            return None
        }
        
        val subName = name.substring( 0, lastDot )
        
        val superHandle = Fieldml_GetObjectByName( fmlHandle, subName )
        if( superHandle == FML_INVALID_HANDLE )
        {
            return None
        }
        
        //At the moment, the only structured types out there are one-deep, and must be forward declared, which means
        //the object should already be in the cache
        
        val superObject = objects( superHandle )
        if( superObject == null )
        {
            return None
        }
        
        if( !superObject.isInstanceOf[Evaluator] )
        {
            return None
        }
        
        val superEval = superObject.asInstanceOf[Evaluator]
        val vType = superEval.valueType
        if( !vType.isInstanceOf[StructuredType] )
        {
            return None
        }
        
        return Some( new SubtypeEvaluatorValueSource( superEval, name.substring( lastDot + 1 ) ) )
    }
    
    
    private def generateExternalEvaluator( objectHandle : Int ) : Evaluator =
    {
        getSubtypeEvaluator( objectHandle ) match
        {
            case s : Some[Evaluator] => s.get
            case None => ExternalEvaluatorGenerator.generateExternalEvaluator( this, objectHandle )
        }
    }
    
    def getExternalEvaluator( objectHandle : Int ) : Evaluator =
    {
        getTypedObject( objectHandle, FHT_EXTERNAL_EVALUATOR, classOf[Evaluator] ) match
        {
            case s : Some[Evaluator] => s.get
            case None => objects( objectHandle ) = generateExternalEvaluator( objectHandle ); objects( objectHandle ).asInstanceOf[Evaluator]
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
    
    
    def getConstantEvaluator( objectHandle : Int ) : ConstantEvaluator =
    {
        getTypedObject( objectHandle, FHT_CONSTANT_EVALUATOR, classOf[ConstantEvaluator] ) match
        {
            case s : Some[ConstantEvaluator] => s.get
            case None => objects( objectHandle ) = ConstantEvaluatorSerializer.extract( this, objectHandle ); objects( objectHandle ).asInstanceOf[ConstantEvaluator]
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
    

    private def addEvaluator( objectHandle : Int, eval : Evaluator ) : Unit =
    {
        objects( objectHandle ) = eval
        
        if( !eval.valueType.isInstanceOf[StructuredType] )
        {
            return
        }
        
        val sType = eval.valueType.asInstanceOf[StructuredType]
        
        for( n <- sType.subNames ) yield n
    }
    
    
    def getArgumentEvaluator( objectHandle : Int ) : ArgumentEvaluator =
    {
        getTypedObject( objectHandle, FHT_ARGUMENT_EVALUATOR, classOf[ArgumentEvaluator] ) match
        {
            case s : Some[ArgumentEvaluator] => s.get
            case None => addEvaluator( objectHandle, ArgumentEvaluatorSerializer.extract( this, objectHandle ) ); objects( objectHandle ).asInstanceOf[ArgumentEvaluator]
        }
    }
    
    
    def getArgumentOrSubtypeEvaluator( objectHandle : Int ) : Evaluator =
    {
        getTypedObject( objectHandle, FHT_ARGUMENT_EVALUATOR, classOf[ArgumentEvaluator] ) match
        {
            case s : Some[ArgumentEvaluator] => s.get
            case None => {
                getSubtypeEvaluator( objectHandle ) match
                {
                    case s : Some[SubtypeEvaluator] => objects( objectHandle ) = s.get; s.get
                    case None => addEvaluator( objectHandle, ArgumentEvaluatorSerializer.extract( this, objectHandle ) ); objects( objectHandle ).asInstanceOf[ArgumentEvaluator]
                }
            }
        }
    }
    
    
    private def getTypedObject[A <: FieldmlObject]( objectHandle : Int, objectType : FieldmlHandleType, clazz : Class[A] ) : Option[A] =
    {
        val actualObjectType = Fieldml_GetObjectType( fmlHandle, objectHandle )
        val name = Fieldml_GetObjectName( fmlHandle, objectHandle )
        
        if( actualObjectType != objectType )
        {
            Fieldml_GetLastError( fmlHandle ) match
            {
                case FML_ERR_UNKNOWN_OBJECT => throw new FmlUnknownObjectException( objectHandle )
                case _ => throw new FmlTypeException( name, actualObjectType, objectType )
            }
        }
        
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
            case ex : ClassCastException => throw new FmlTypeException( name, actualObjectType, objectType )
        }
        
        return None
    }
}
