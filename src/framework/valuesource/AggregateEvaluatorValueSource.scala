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
    
    private val indexValues = for( i <- 1 to indexType.bounds.elementCount ) yield Some( new EnsembleValue( indexType, i ) )
    
    override def evaluate( state : EvaluationState ) : Option[Value] =
    {
        state.pushAndApply( binds.toSeq ++ indexBinds.toSeq.map( ( t : Tuple2[Int, AbstractEvaluator] ) => Tuple2[AbstractEvaluator, Evaluator]( t._2, indexEvaluator ) ) )
        
        val values = new Array[Double]( indexValues.size )
        
        for( i <- indexValues )
        {
            componentEvaluators.get( i.get.eValue ) match
            {
                case e : Some[Evaluator] => {
                    indexEvaluator.value = i;
                    e.get.evaluate( state ) match
                    {
                        case v : Some[Value] => values( i.get.eValue - 1 ) = v.get.cValue( 0 )
                        case _ => state.pop; return None
                    }
                }
                case _ => state.pop; return None
            }
        }

        state.pop()
        return Some( new ContinuousValue( valueType, values ) )
    }

}



/*
  bind library.ensemble.rc.3d[Evaluator] -> ([ 1])[ConstantValueSource]
  bind test.mesh[Evaluator] -> ([ 2 0.0 0.0 ])[ConstantValueSource]
  bind library.parameters.bilinear_lagrange.variable[Evaluator] -> test.bilinear_parameters[Evaluator]
test.mesh delegating to Constant([ 2 0.0 0.0 ])
test.interpolator_v2 delegating to library.fem.bilinear_lagrange
  bind library.xi.2d.variable[Evaluator] -> test.mesh.xi[Evaluator]
library.xi.2d.variable delegating to test.mesh.xi
test.mesh delegating to Constant([ 2 0.0 0.0 ])
library.parameters.bilinear_lagrange.variable delegating to test.bilinear_parameters
>>> aggregate test.bilinear_parameters evaluating...4
   bind test.nodes[Evaluator] -> test.connectivity[Evaluator]
   bind variables.bilinear.index[Evaluator] -> (test.bilinear_parameters.index)[VariableValueSource]
   >>> parameters test.parameters evaluating 2 indexes...
      test.nodes delegating to test.connectivity
      >>> parameters test.connectivity evaluating 2 indexes...
         test.mesh delegating to Constant([ 2 0.0 0.0 ])
         variables.bilinear.index delegating to test.bilinear_parameters.index
      <<< parameters test.connectivity = Some([ 2])
      library.ensemble.rc.3d delegating to Constant([ 1])
   <<< parameters test.parameters = Some([ 0.0 ])
     Component Some([ 1]) = Some([ 0.0 ])
   >>> parameters test.parameters evaluating 2 indexes...
      Abstract evaluator test.nodes is not bound
      test.parameters's index evaluator test.nodes returned nothing
     Component Some([ 2]) failed
*** piecewise(2) = None

*/
