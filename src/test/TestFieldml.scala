package test

import scala.collection.mutable.ArrayBuffer

import fieldml._
import fieldml.domain._
import fieldml.domain.bounds._

import fieldml.jni.FieldmlApi._

import util._
import util.region._

object TestFieldml
{
    def main( args: Array[String] ): Unit =
    {
        val region = new UserRegion( "test" )

        val ensemble = region.createEnsembleDomain( "test.ensemble", new ContiguousEnsembleBounds( 10 ), false )
    
        val rc3ensemble : EnsembleDomain = region.getObject( "library.ensemble.rc.3d" )
        
        val rc3domain = region.createContinuousDomain( "test.domain.rc3" , rc3ensemble )

        val rc3ensemble2 : EnsembleDomain = region.getObject( "library.ensemble.rc.3d" )

        System.out.println( ensemble.name )
        
        region.serialize()
    }
}
