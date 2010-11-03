package framework

import fieldml.evaluator._
import fieldml.valueType._

import framework.valuesource._

import value.ContinuousValue

class FunctionEvaluator( name : String, val function : ( Array[Double], Array[Double] ) => Array[Double], val var1 : AbstractEvaluator, val var2 : AbstractEvaluator, valueType : ContinuousType )
    extends Evaluator( name, valueType )
{
    val variables = Seq[AbstractEvaluator]( var1, var2 )

    
    def evaluate( state : EvaluationState ) : Option[ContinuousValue] =
    {
        val arg1 = var1.evaluate( state ) match { case s : Some[ContinuousValue] => s.get.cValue; case _ => return None }
        val arg2 = var2.evaluate( state ) match { case s : Some[ContinuousValue] => s.get.cValue; case _ => return None }

        return Some( new ContinuousValue( valueType, function( arg1, arg2 ) ) )
    }
}
