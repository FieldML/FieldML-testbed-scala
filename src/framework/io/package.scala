package framework.io

import fieldml.domain.ContinuousDomain
import fieldml.domain.EnsembleDomain
import fieldml.domain.MeshDomain
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
    
    implicit def continuousDomainSerializer( domain : ContinuousDomain ) = new ContinuousDomainSerializer( domain )
    implicit def ensembleDomainSerializer( domain : EnsembleDomain ) = new EnsembleDomainSerializer( domain )
    implicit def meshDomainSerializer( domain : MeshDomain ) = new MeshDomainSerializer( domain )
    implicit def piecewiseEvaluatorSerializer( evaluator : PiecewiseEvaluator ) = new PiecewiseEvaluatorSerializer( evaluator )
    implicit def parameterEvaluatorSerializer( evaluator : ParameterEvaluator ) = new ParameterEvaluatorSerializer( evaluator )
    implicit def referenceEvaluatorSerializer( evaluator : ReferenceEvaluator ) = new ReferenceEvaluatorSerializer( evaluator )
}
