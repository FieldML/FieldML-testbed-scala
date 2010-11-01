package framework

import scala.collection.mutable.Map
import scala.collection.mutable.Stack

import fieldml._
import fieldml.valueType._
import fieldml.evaluator._

import value._
import valuesource._

import util.exception._

class Context( val name : String )
{
    private val values = Map[FieldmlObject, ValueSource]()
    
    private val binds = Map[AbstractEvaluator, Evaluator]()
    
    
    override def toString() : String =
    {
        return name + "[Context]"
    }
    

    def update( valueType : ValueType, value : Value )
    {
        values.get( valueType ) match
        {
            case v : Some[ConstantValueSource] => add( new ConstantValueSource( valueType, value ) )
            case None => add( new ConstantValueSource( valueType, value ) )
            case _ => throw new FmlException( valueType + " already has a value definition at this scope: " + values.get( valueType ) )
        }
    }
    
    
    def add( valueSource : ValueSource )
    {
        values( valueSource.valueType ) = valueSource
    }
    
    
    private def aliasEnsembleType( valueType : EnsembleType, obj : FieldmlObject ) =
    {
        var valueType2 : ValueType = null
        
        obj match
        {
            case d : EnsembleType => valueType2 = d
            case e : Evaluator => valueType2 = e.valueType
        }
        
        if( valueType2 == valueType )
        {
            values( valueType ) = new AliasValueSource( valueType, obj )
        }
        else
        {
            throw new FmlInvalidObjectException( "Alias conflict: " + valueType + " " + valueType2 )
        }
    }

    
    private def aliasContinuousType( valueType : ContinuousType, obj : FieldmlObject ) : Unit =
    {
        var valueType2 : ValueType = null
        
        obj match
        {
            case d : ContinuousType => valueType2 = d
            case e : Evaluator => valueType2 = e.valueType
        }
        
        if( valueType2 == valueType )
        {
            values( valueType ) = new AliasValueSource( valueType, obj )
            return
        }
        
        val componentType : EnsembleType = valueType.componentType
        var componentType2 : EnsembleType = null
        
        valueType2 match
        {
            case d: ContinuousType => componentType2 = d.componentType
            case e : EnsembleType => throw new FmlInvalidObjectException( "Alias conflict: " + valueType + " " + valueType2 )
        }
        
        if( componentType == componentType2 )
        {
            values( valueType ) = new AliasValueSource( valueType, obj )
        }
        else if( ( componentType != null ) && ( componentType2 == null ) )
        {
            values( valueType ) = new VectorizedAliasValueSource( valueType, componentType, obj )
        }
        else
        {
            throw new FmlInvalidObjectException( "Alias conflict: " + valueType + " " + valueType2 )
        }
    }

    
    def alias( valueType : ValueType, obj : FieldmlObject ) =
    {
        valueType match
        {
            case e : EnsembleType => aliasEnsembleType( e, obj )
            case c : ContinuousType => aliasContinuousType( c, obj )
        }
    }

    
    def apply( domain : FieldmlObject ) : Option[ValueSource] =
    {
        return values.get( domain )
    }
    
    
    def getBind( evaluator : AbstractEvaluator ) : Option[Evaluator] =
    {
        binds.get( evaluator )
    }
    
    
    def setBind( evaluator : AbstractEvaluator, source : Evaluator ) : Unit =
    {
        binds( evaluator ) = source
    }
}
