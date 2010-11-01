package fieldml.valueType

import fieldml._

class ValueType( name : String )
    extends FieldmlObject( name )
{
    override def toString() : String =
    {
        return name + "[Type]"
    }
}