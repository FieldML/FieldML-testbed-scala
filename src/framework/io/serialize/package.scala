package framework.io

import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType
import fieldml.valueType.MeshType
import fieldml.evaluator.PiecewiseEvaluator
import fieldml.evaluator.ParameterEvaluator
import fieldml.evaluator.ReferenceEvaluator

import util.exception._

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._

package object serialize
{
    def GetNamedObject( handle : Long, name : String ) : Int =
    {
        val objectHandle = Fieldml_GetNamedObject( handle, name )
        if( objectHandle == FML_INVALID_HANDLE )
        {
            //TODO Use the right region name.
            throw new FmlUnknownObjectException( name, "" )
        }
        
        return objectHandle
    }
    
    implicit def continuousTypeSerializer( valueType : ContinuousType ) = new ContinuousTypeSerializer( valueType )
    implicit def ensembleTypeSerializer( valueType : EnsembleType ) = new EnsembleTypeSerializer( valueType )
    implicit def meshTypeSerializer( valueType : MeshType ) = new MeshTypeSerializer( valueType )
    implicit def piecewiseEvaluatorSerializer( evaluator : PiecewiseEvaluator ) = new PiecewiseEvaluatorSerializer( evaluator )
    implicit def parameterEvaluatorSerializer( evaluator : ParameterEvaluator ) = new ParameterEvaluatorSerializer( evaluator )
    implicit def referenceEvaluatorSerializer( evaluator : ReferenceEvaluator ) = new ReferenceEvaluatorSerializer( evaluator )
}
