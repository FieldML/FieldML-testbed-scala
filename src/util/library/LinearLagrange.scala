package util.library

import fieldml.evaluator.ContinuousEvaluator
import fieldml.domain.ContinuousDomain

class LinearLagrange( name : String, domain : ContinuousDomain, val dimensions : Int )
    extends ContinuousEvaluator( name, domain )
{

}