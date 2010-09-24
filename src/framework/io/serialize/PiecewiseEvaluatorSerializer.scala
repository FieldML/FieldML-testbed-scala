package framework.io.serialize

import fieldml.evaluator.PiecewiseEvaluator

import util.exception._

import fieldml.jni.FieldmlApi._

class PiecewiseEvaluatorSerializer( val evaluator : PiecewiseEvaluator )
{
    def insert( handle : Long ) : Unit =
    {
        val indexHandle = GetNamedObject( handle, evaluator.index.name )
        val valueHandle = GetNamedObject( handle, evaluator.valueDomain.name )
        
        val objectHandle = Fieldml_CreateContinuousPiecewise( handle, evaluator.name, indexHandle, valueHandle )
    }
}
