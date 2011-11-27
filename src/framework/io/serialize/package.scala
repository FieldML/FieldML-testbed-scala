package framework.io

import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType
import fieldml.valueType.MeshType
import fieldml.valueType.BooleanType
import fieldml.DataSource
import fieldml.DataResource
import fieldml.evaluator.Evaluator
import fieldml.evaluator.ArgumentEvaluator
import fieldml.evaluator.AggregateEvaluator
import fieldml.evaluator.ConstantEvaluator
import fieldml.evaluator.PiecewiseEvaluator
import fieldml.evaluator.ParameterEvaluator
import fieldml.evaluator.ReferenceEvaluator

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

package object serialize
{
    def GetNamedObject( handle : Int, name : String ) : Int =
    {
        val objectHandle = Fieldml_GetObjectByName( handle, name )
        if( objectHandle == FML_INVALID_HANDLE )
        {
            //TODO Use the right region name.
            throw new FmlUnknownObjectException( name, "" )
        }
        
        return objectHandle
    }
    
    
    def GetBinds( source : Deserializer, objectHandle : Int ) : Seq[Tuple2[Evaluator, Evaluator]] =
    {
        val bindCount = Fieldml_GetBindCount( source.fmlHandle, objectHandle )
        
        for( i <- 1 to bindCount;
            variable = source.getArgumentOrSubtypeEvaluator( Fieldml_GetBindArgument( source.fmlHandle, objectHandle, i ) );
            evaluator = source.getEvaluator( Fieldml_GetBindEvaluator( source.fmlHandle, objectHandle, i ) )
            )
            yield Tuple2( variable, evaluator )
    }
    

    implicit def continuousTypeSerializer( valueType : ContinuousType ) = ContinuousTypeSerializer
    implicit def ensembleTypeSerializer( valueType : EnsembleType ) = EnsembleTypeSerializer
    implicit def meshTypeSerializer( valueType : MeshType ) = MeshTypeSerializer
    implicit def booleanTypeSerializer( valueType : BooleanType ) = BooleanTypeSerializer
    implicit def dataResourceSerializer( dataResource : DataResource ) = DataResourceSerializer
    implicit def argumentEvaluatorSerializer( evaluator : ArgumentEvaluator ) = ArgumentEvaluatorSerializer
    implicit def piecewiseEvaluatorSerializer( evaluator : PiecewiseEvaluator ) = PiecewiseEvaluatorSerializer
    implicit def parameterEvaluatorSerializer( evaluator : ParameterEvaluator ) = ParameterEvaluatorSerializer
    implicit def aggregateEvaluatorSerializer( evaluator : AggregateEvaluator ) = AggregateEvaluatorSerializer
    implicit def referenceEvaluatorSerializer( evaluator : ReferenceEvaluator ) = ReferenceEvaluatorSerializer
    implicit def constantEvaluatorSerializer( evaluator : ConstantEvaluator ) = ConstantEvaluatorSerializer
}
