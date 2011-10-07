package framework.valuesource

import fieldml.evaluator.ArgumentEvaluator
import fieldml.evaluator.AggregateEvaluator
import fieldml.evaluator.Evaluator
import fieldml.valueType.ContinuousType
import fieldml.valueType.EnsembleType

import framework.value.Value
import framework.value.ContinuousValue
import framework.value.EnsembleValue
import framework.Context
import framework.EvaluationState

class AggregateEvaluatorValueSource( name : String, valueType : ContinuousType )
    extends AggregateEvaluator( name, valueType )
    with ValueSource
{
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        val indexType = indexBinds(1).valueType.asInstanceOf[EnsembleType]
        
        val indexEvaluator = new VariableValueSource( name + ".index", indexType )
        
        val indexValues = ( for( i <- 1 to indexType.elementCount ) yield new EnsembleValue( indexType, i ) ).toArray
        
        state.pushAndApply( name, binds.toSeq ++ indexBinds.toSeq.map( ( t : Tuple2[Int, Evaluator] ) => Tuple2[Evaluator, Evaluator]( t._2, indexEvaluator ) ) )

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
