package test

import scala.collection.mutable.ArrayBuffer
import scala.util.parsing.combinator._
import scala.util.matching.Regex
import java.io.FileWriter
import fieldml._
import fieldml.valueType._
import fieldml.evaluator._
import framework.datastore._
import framework.value._
import framework._
import fieldml.jni.FieldmlApi._
import util.ColladaExporter
import framework.region._
import valuesource.ParameterEvaluatorValueSource

object EvaluateFieldAt
{
  def main( argv : Array[String] ) : Unit = {
    if (argv.size == 3) {
      println(argv(2))
      ExpressionParser.parse(argv(2)) match {
        case ExpressionParser.Success(res, _) =>
          evaluateFieldAt(argv(0), argv(1), res)
        case ExpressionParser.NoSuccess(msg, _) =>
          println("Error parsing expression: " + msg)
      }
    }
    else {
      println("Usage: EvaluateFieldAt xmlFilePath region expression\r\n" +
              "expression: empty | bind name value expression | evaluate name expression\r\n" +
              "name, region: dotted FieldML identifiers\r\n" +
              "value: A series of values (value production below) surrounded by []\r\n" +
              "value: meshValue int listOfDoubleValues | ensembleValue int | continuousValue listOfDoubleValues")
    }
  }

  def evaluateFieldAt( xmlFilePath : String, regionName : String, exprs : List[FieldAction]) : Unit = {
    val region = UserRegion.fromFile(regionName, xmlFilePath)
    exprs.map(expr =>
      {
        expr match {
          case BindParameterAction(n, v) => {
            val argEvaluator : ArgumentEvaluator = region.getObject(n)
            v match {
              case MeshMakeValue(el, xi) => region.bind(argEvaluator, el, xi : _*)
              case EnsembleMakeValue(ens) => region.bind(argEvaluator, ens)
              case ContinuousMakeValue(vals) => region.bind(argEvaluator, vals : _*)
            }
          }
          case EvaluateAction(n) => {
            val evalField : Evaluator = region.getObject(n)
            println("eval(" + n + ") = " + region.evaluate(evalField))
          }
        }
      }
              )
    ()
  }

  abstract class FieldAction
  case class BindParameterAction(bindArgumentName : String, bindValues : MakeValue) extends FieldAction
  case class EvaluateAction(evaluateField : String) extends FieldAction
  abstract class MakeValue
  case class MeshMakeValue(meshElement : Int, meshXIs : List[Double]) extends MakeValue
  case class EnsembleMakeValue(ensembleID : Int) extends MakeValue
  case class ContinuousMakeValue(values : List[Double]) extends MakeValue

  object ExpressionParser extends RegexParsers {
    def parse(s : String) = parseAll(expressionParser, s)
    def expressionParser = rep(actionParser)
    def actionParser = bindActionParser | evaluateActionParser
    def bindActionParser = "bind" ~> (regex(new Regex("[A-Z|a-z|\\.]+"))) ~ valueParser ^^
      (p => p match { case n~v => new BindParameterAction(n, v) })
    def evaluateActionParser = "evaluate" ~> commit(regex(new Regex("[A-Z|a-z|\\.]+")) ^^
      (v => new EvaluateAction(v)))
    def valueParser = meshValueParser | ensembleValueParser | continuousValueParser
    def meshValueParser = "meshValue" ~> commit(intParser ~ rep(doubleParser) ^^
      (v => v match { case el ~ xi => new MeshMakeValue(el, xi) }))
    def ensembleValueParser = "ensembleValue" ~> commit(intParser ^^
      (ensv => new EnsembleMakeValue(ensv)))
    def continuousValueParser = "continuousValue" ~> commit(rep(doubleParser) ^^ (ensv => new ContinuousMakeValue(ensv)))
    def intParser = commit(regex(new Regex("-?[0-9]+")) ^^ Integer.parseInt)
    def doubleParser = commit(regex(new Regex("-?[0-9|\\.]+")) ^^ java.lang.Double.parseDouble)
  }
}
