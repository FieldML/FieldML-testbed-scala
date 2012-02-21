package framework

import scala.collection.immutable.Map

import fieldml._
import fieldml.valueType._
import fieldml.evaluator._

import value._
import valuesource._

import util.exception._

class Context( val location : String,
    parentBinds : Map[Evaluator, Tuple2[Evaluator, Context]],
    initialBinds : Seq[Tuple2[Evaluator, Evaluator]] )
{
    def this( location : String, otherContext : Option[Context], initialBinds : Seq[Tuple2[Evaluator, Evaluator]] )
    {
        this( location, otherContext.map(_.binds).getOrElse(Map.empty), initialBinds )
    }
    
    private val binds : Map[Evaluator, Tuple2[Evaluator, Context]] =
      parentBinds ++ initialBinds.map( x => Tuple2( x._1, Tuple2( x._2, this ) ) )
    
    def getBind( evaluator : Evaluator ) : Option[Evaluator] =
    {
        binds.get( evaluator ).map( _._1 )
    }
    
    
    def getBindContext( evaluator : Evaluator ) : Option[Context] =
    {
        binds.get( evaluator ).map( _._2 )
    }
}
