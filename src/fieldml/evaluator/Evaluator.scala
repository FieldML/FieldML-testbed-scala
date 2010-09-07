package fieldml.evaluator

import fieldml.domain.Domain
import fieldml.FieldmlObject

abstract class Evaluator( name : String, val valueDomain : Domain )
    extends FieldmlObject( name )
{
    override def toString() : String =
    {
        return name + "[Evaluator]"
    }
}
