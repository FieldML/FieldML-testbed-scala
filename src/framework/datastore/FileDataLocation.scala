package framework.datastore

import fieldml.jni.DataFileType

class FileDataLocation( val filename : String, val offset : Int, val dataType : DataFileType )
    extends DataLocation
{
}
