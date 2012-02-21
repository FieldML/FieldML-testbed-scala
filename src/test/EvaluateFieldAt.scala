package test

import scala.collection.mutable.ArrayBuffer
import scala.math._
import scala.util.parsing.combinator._
import scala.util.matching.Regex
import java.io._
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
import javax.imageio._
import edu.wlu.cs.levy.CG.KDTree
import java.awt.image.BufferedImage
import java.awt.Color

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
              "                  | evaluateImage filename outputEval inputEval\r\n" +
              "name, region: dotted FieldML identifiers\r\n" +
              "value: A series of values (value production below) surrounded by []\r\n" +
              "value: meshValue int listOfDoubleValuesSpaceSeparated | ensembleValue int | continuousValue listOfDoubleValues")
    }
  }

  def evaluateFieldAt( xmlFilePath : String, regionName : String, exprs : List[FieldAction]) : Unit = {
    val region = UserRegion.fromFile(regionName, xmlFilePath)
    println(region)
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
          case EvaluateImageAction(fn, inputEvalName, valueEvalName, coordEvalName) => {
            // XXX this needs more error checking (e.g. wrong argument types),
            // and it should do something better than assume a unit square mesh.
            val imageWidth : Int = 300
            val inputEval : ArgumentEvaluator = region.getObject(inputEvalName)
            val valueEval : Evaluator = region.getObject(valueEvalName)
            val coordEval : Evaluator = region.getObject(coordEvalName)
            val inputMesh : MeshType = inputEval.valueType.asInstanceOf[MeshType]
            val elementCount : Int = inputMesh.elementType.elementCount
            val evalCount : Int = (imageWidth.asInstanceOf[Double] * 2.0 / sqrt(elementCount)).asInstanceOf[Int]
            val evalRange = Range(0, evalCount, 1)
            val kdt : KDTree[Double] = new KDTree(2)
            var minX : Double = Double.MaxValue
            var minY : Double = Double.MaxValue
            var maxX : Double = Double.MinValue
            var maxY : Double = Double.MinValue
            var minZ : Double = Double.MaxValue
            var maxZ : Double = Double.MinValue
            for (elem <- inputMesh.elementType.elementSet.toArray; xi1f <- evalRange; xi2f <- evalRange) {
              val xi1 = xi1f.asInstanceOf[Double] / evalCount.asInstanceOf[Double]
              val xi2 = xi2f.asInstanceOf[Double] / evalCount.asInstanceOf[Double]
              region.bind(inputEval, elem, xi1, xi2)
              val Some(v : ContinuousValue) = region.evaluate(valueEval)
              val Some(c : ContinuousValue) = region.evaluate(coordEval)
              kdt.insert(c.value, v.value(0))
              if (c.value(0) < minX) minX = c.value(0)
              if (c.value(0) > maxX) maxX = c.value(0)
              if (c.value(1) < minY) minY = c.value(1)
              if (c.value(1) > maxY) maxY = c.value(1)
              if (v.value(0) < minZ) minZ = v.value(0)
              if (v.value(0) > maxZ) maxZ = v.value(0)
            }
            /* Debug code so we don't need to wait every time to test the below code...
            kdt.insert(Array(0,0), 10)
            minX = 100
            maxX = 200
            minY = 50
            maxY = 300
            minZ = 5
            maxZ = 20 */

            val imageHeight : Int = round(imageWidth.asInstanceOf[Float] * (maxY - minY).asInstanceOf[Float] / (maxX - minX).asInstanceOf[Float])
            val bim : BufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
            for (x <- Range(0, imageWidth); y <- Range(0, imageHeight)) {
              val realx : Double = minX + (maxX - minX) * (x.asInstanceOf[Double] / imageWidth.asInstanceOf[Double])
              val realy : Double = minY + (maxY - minY) * (y.asInstanceOf[Double] / imageHeight.asInstanceOf[Double])
              val realz : Double = kdt.nearest(Array(realx, realy))
              val hue : Float = ((realz - minZ) / (maxZ - minZ) * 0.7f).asInstanceOf[Float]
              val rgb : Int = Color.HSBtoRGB(hue, 1.0f, 1.0f)
              bim.setRGB(x, y, rgb)
            }
            ImageIO.write(bim, "PNG", new File(fn));
          }
        }
      }
              )
    ()
  }

  abstract class FieldAction
  case class BindParameterAction(bindArgumentName : String, bindValues : MakeValue) extends FieldAction
  case class EvaluateAction(evaluateField : String) extends FieldAction
  case class EvaluateImageAction(storeTo : String, inputEval : String, valueEval : String, coordEval : String) extends FieldAction
  abstract class MakeValue
  case class MeshMakeValue(meshElement : Int, meshXIs : List[Double]) extends MakeValue
  case class EnsembleMakeValue(ensembleID : Int) extends MakeValue
  case class ContinuousMakeValue(values : List[Double]) extends MakeValue

  object ExpressionParser extends RegexParsers {
    def parse(s : String) = parseAll(expressionParser, s)
    def expressionParser = rep(actionParser)
    def actionParser = bindActionParser | evaluateImageActionParser | evaluateActionParser
    def bindActionParser = "bind" ~> (regex(new Regex("[A-Z|a-z][A-Z|a-z|\\.|0-9|_]*"))) ~ valueParser ^^
      (p => p match { case n~v => new BindParameterAction(n, v) })
    def evaluateActionParser = "evaluate" ~> commit(regex(new Regex("[A-Z|a-z][A-Z|a-z|\\.|0-9|_]*")) ^^
      (v => new EvaluateAction(v)))
    def evaluateImageActionParser =
      "evaluateImage" ~> commit(regex(new Regex("[A-Z|a-z|\\.|0-9|_|\\-|/]+")) ~
                                regex(new Regex("[A-Z|a-z][A-Z|a-z|\\.|0-9|_]*")) ~
                                regex(new Regex("[A-Z|a-z][A-Z|a-z|\\.|0-9|_]*")) ~
                                regex(new Regex("[A-Z|a-z][A-Z|a-z|\\.|0-9|_]*")) ^^
                                (p => p match { case fn~inputEval~valueEval~coordEval => new EvaluateImageAction(fn, inputEval, valueEval,
                                                                                                                 coordEval) }))
                                    
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
