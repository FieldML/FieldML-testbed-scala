package framework.region

import scala.collection.mutable.Map

import fieldml.FieldmlObject
import fieldml.valueType._
import fieldml.valueType.bounds._
import fieldml.evaluator._

import util.exception._

import framework.valuesource.AbstractEvaluatorValueSource

import framework.valuesource._

import framework.Context
import framework.EvaluationState
import framework.value.Value
import framework.value.ContinuousValue
import framework.value.EnsembleValue
import framework.value.MeshValue

abstract class Region( val name : String )
{
    protected val objects = Map[String, FieldmlObject]()

    private val companions = Map[ValueType, AbstractEvaluatorValueSource]()
    
    private val binds = Map[ AbstractEvaluator, Evaluator ]()
    
    //TODO Use region names
    def getObject[A <: FieldmlObject]( objectName : String ) : A =
    {
        val result = objects.get( objectName )
        
        result match
        {
            case s : Some[A] => return s.get
            case s : Some[_] => throw new FmlTypeException( s.get, null ) //MUSTDO Fix
            case None => throw new FmlUnknownObjectException( objectName, name )
        }
    }
    
    
    
    def getCompanionVariable( vType : ValueType ) : AbstractEvaluator =
    {
        companions.get( vType ) match
        {
            case s : Some[AbstractEvaluatorValueSource] => s.get
            case None => companions( vType ) = new AbstractEvaluatorValueSource( vType.name + ".variable", vType ); companions( vType )
        }
    }
    
    def evaluate( evaluator : Evaluator ) : Option[Value] =
    {
        val state = new EvaluationState()
        
        state.pushAndApply( binds.toSeq )
        
        return evaluator.evaluate( state )
    }
    
    
    def bind( variable : AbstractEvaluator, element : Int, xi : Double* ) =
    {
        variable.valueType match
        {
            case m : MeshType => binds( variable ) = new ConstantValueSource( new MeshValue( m, element, xi:_* ) )
            case _ =>
        }
    }
    
    
    def bind( variable : AbstractEvaluator, value : Int ) =
    {
    }
    
    
    def bind( variable : AbstractEvaluator, value : Double* ) =
    {
    }
}
