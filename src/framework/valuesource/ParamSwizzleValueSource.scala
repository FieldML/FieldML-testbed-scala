package framework.valuesource

import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType
import fieldml.valueType.ContinuousType

import framework.value.ContinuousValue
import framework.ParamSwizzle
import framework.EvaluationState

class ParamSwizzleValueSource( name : String, valueType : ValueType, source : Evaluator, swizzle : Array[Int] )
    extends ParamSwizzle( name, valueType, source, swizzle )
    with ValueSource
{
    private val buffer = new Array[Double]( swizzle.size )
    
    def evaluate( state : EvaluationState ) : Option[ContinuousValue] =
    {
        val rawVal = source.evaluate( state )
        
        if( rawVal == None )
        {
            return None
        }
        
        for( i <- 0 until swizzle.size )
        {
            buffer( i ) = rawVal.get.cValue( swizzle( i ) - 1 )
        }
        
        return new Some( new ContinuousValue( valueType.asInstanceOf[ContinuousType], buffer ) )
    }
}
