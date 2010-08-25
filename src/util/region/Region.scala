package util.region

import scala.collection.mutable.Map

import fieldml.FieldmlObject
import fieldml.domain._
import fieldml.domain.bounds._

import util.exception._

abstract class Region( val name : String, private val dependancies : Region* )
{
    protected val objects = Map[String, FieldmlObject]()
    
    //TODO Use region names
    def getObject[A <: FieldmlObject]( objectName : String ) : A =
    {
        val result = getFromSelfOrDependancies( objectName )
        
        result match
        {
            case s : Some[A] => return s.get
            case s : Some[_] => throw new FmlTypeException( s.get, null ) //MUSTDO Fix
            case None => throw new FmlUnknownObjectException( objectName, name )
        }
    }
    
    
    private def getFromSelfOrDependancies[A <: FieldmlObject]( objectName : String ) : Option[FieldmlObject] =
    {
        objects.get( objectName ) match
        {
            case s : Some[_] => return s
            case None => return getFromDependancies( objectName )
        }
    }

    
    private def getFromDependancies[A <: FieldmlObject]( objectName : String ) : Option[FieldmlObject] =
    {
        val r2 = dependancies
        
        for(
            r <- dependancies;
            o <- r.getFromSelfOrDependancies( objectName )
            )
        {
            return Some(o)
        }
        
        return None
    }
}
