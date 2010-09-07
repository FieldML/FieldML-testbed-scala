package fieldml.domain

import fieldml._

class Domain( name : String )
    extends FieldmlObject( name )
{
    override def toString() : String =
    {
        return name + "[Domain]"
    }
}