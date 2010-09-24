package util

import scala.collection.mutable.HashMap

class DefaultingHashMap[A,B <: AnyRef]
    extends HashMap[A,B]
{
    private var _default : Option[B] = None
    
    
    def default_=( value : B ) : Unit =
    {
        if( value == null )
        {
            _default = None
        }
        else
        {
            _default = Some( value )
        }
    }
    
    
    def default : Option[B] = _default


    override def get( key : A ) : Option[B] =
    {
        super.get( key ) match
        {
            case s : Some[B] => return s
            case None => return _default
        }
    }
    
    
    override def put( key: A, value: B ) : Option[B] =
    {
        if( value == null )
        {
            return remove( key )
        }
        else
        {
            return super.put( key, value )
        }
    }
}
