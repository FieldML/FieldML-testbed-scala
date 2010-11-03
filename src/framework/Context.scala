package framework

import scala.collection.mutable.Map
import scala.collection.mutable.Stack

import fieldml._
import fieldml.valueType._
import fieldml.evaluator._

import value._
import valuesource._

import util.exception._

class Context()
{
    private val binds = Map[AbstractEvaluator, Evaluator]()
    
    
    def getBind( evaluator : AbstractEvaluator ) : Option[Evaluator] =
    {
        binds.get( evaluator )
    }
    
    
    def setBind( evaluator : AbstractEvaluator, source : Evaluator ) : Unit =
    {
        binds( evaluator ) = source
    }
}
