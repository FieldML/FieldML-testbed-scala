package fieldml.evaluator

import scala.collection.mutable.ArrayBuffer

import fieldml.valueType.ValueType
import fieldml.FieldmlObject

abstract class Evaluator( name : String, val valueType : ValueType )
    extends FieldmlObject( name )
{
    def variables : Iterable[AbstractEvaluator]

    
    override def toString() : String =
    {
        return name + "[" + getClass.getSimpleName + "]"
    }
}
