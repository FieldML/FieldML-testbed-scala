package framework.valuesource

import fieldml.evaluator.AbstractEvaluator
import fieldml.evaluator.AggregateEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ContinuousType

import framework.value.Value
import framework.value.ContinuousValue
import framework.value.EnsembleValue
import framework.Context
import framework.EvaluationState

class AggregateEvaluatorValueSource( name : String, valueType : ContinuousType )
    extends AggregateEvaluator( name, valueType )
    with ValueSource
{
    private val indexType = valueType.componentType
    
    private val indexEvaluator = new VariableValueSource( name + ".index", indexType )
    
    private val indexValues = ( for( i <- 1 to indexType.bounds.elementCount ) yield new EnsembleValue( indexType, i ) ).toArray
    
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        state.pushAndApply( binds.toSeq ++ indexBinds.toSeq.map( ( t : Tuple2[Int, AbstractEvaluator] ) => Tuple2[AbstractEvaluator, Evaluator]( t._2, indexEvaluator ) ) )

        val values = for( i <- indexValues;
             e <- componentEvaluators.get( i.eValue );
             v <- { indexEvaluator.value = Some( i ); e.evaluate( state ) } ) yield v

        state.pop()
        
        if( values.size != indexValues.size )
        {
            println( "Some aggregate evaluators in " + name + " failed" )
            return None
        }
        
        return Some( new ContinuousValue( valueType, values.flatMap( _.cValue ) ) )
    }

}
