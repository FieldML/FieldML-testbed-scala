package test

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType._
import fieldml.jni.DataLocationType._

object TestDofVector 
{
    private def exportExample1() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_1" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, nodesType, 8 )
        
        val bilinearNodes = Fieldml_GetObjectByName( fml, "library.local_nodes.square.2x2" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "library.ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshType, "library.shape.square" )
        Fieldml_SetContiguousBoundsCount( fml, meshType, 3 )
        
        val elementsType = Fieldml_GetMeshElementType( fml, meshType )
        val xiType = Fieldml_GetMeshXiType( fml, meshType )

        val connectivity = Fieldml_CreateParametersEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, connectivity, bilinearNodes, 0 )
        Fieldml_AddSemidenseIndexEvaluator( fml, connectivity, elementsType, 0 )
        
        Fieldml_SetParameterDataLocation( fml, connectivity, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, connectivity, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, connectivity, "1 2 5 6\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "2 3 6 7\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "3 4 7 8", 8 );
        
        Fieldml_SetMeshConnectivity( fml, meshType, connectivity, bilinearNodes )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "library.real.1d" )
        val nodalParams = Fieldml_CreateParametersEvaluator( fml, "test.nodal_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, nodalParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, nodalParams, nodesType, 1 )
        Fieldml_SetParameterDataLocation( fml, nodalParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, nodalParams, "1 0.0 ", 6 )
        Fieldml_AddParameterInlineData( fml, nodalParams, "2 0.5 ", 6 )
        Fieldml_AddParameterInlineData( fml, nodalParams, "3 1.0 ", 6 )
        Fieldml_AddParameterInlineData( fml, nodalParams, "4 1.5 ", 6 )
        
        val elementParams = Fieldml_CreateParametersEvaluator( fml, "test.element_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, elementParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, elementParams, elementsType, 1 )
        Fieldml_SetParameterDataLocation( fml, elementParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, elementParams, "2 2.0 ", 6 )

        val globalParams = Fieldml_CreateParametersEvaluator( fml, "test.global_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, globalParams, DESCRIPTION_SEMIDENSE )
        Fieldml_SetParameterDataLocation( fml, globalParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, globalParams, "3.0 ", 4 )
        
        val bilinearNodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_nodal_params", nodalParams )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesType, connectivity )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "library.fem.bilinear_lagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "library.parameters.bilinear_lagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "library.xi.2d" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example1.xml" )
    }

    
    private def exportExample2() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_2" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, nodesType, 8 )
        
        val bilinearNodes = Fieldml_GetObjectByName( fml, "library.local_nodes.square.2x2" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "library.ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshType, "library.shape.square" )
        Fieldml_SetContiguousBoundsCount( fml, meshType, 3 )

        val elementsType = Fieldml_GetMeshElementType( fml, meshType )
        val xiType = Fieldml_GetMeshXiType( fml, meshType )

        val connectivity = Fieldml_CreateParametersEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, connectivity, bilinearNodes, 0 )
        Fieldml_AddSemidenseIndexEvaluator( fml, connectivity, elementsType, 0 )
        
        Fieldml_SetParameterDataLocation( fml, connectivity, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, connectivity, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, connectivity, "1 2 5 6\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "2 3 6 7\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "3 4 7 8", 8 );
        
        Fieldml_SetMeshConnectivity( fml, meshType, connectivity, bilinearNodes )

        val dofIndexType = Fieldml_CreateEnsembleType( fml, "test.dof_number", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, dofIndexType, 6 )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "library.real.1d" )

        val dofParams = Fieldml_CreateParametersEvaluator( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, dofParams, dofIndexType, 0 )
        Fieldml_SetParameterDataLocation( fml, dofParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, dofParams, "0.0 0.5 1.0 1.5 2.0 3.0 ", 24 )
        
        val nodalIndexes = Fieldml_CreateParametersEvaluator( fml, "test.nodal_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, nodalIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, nodalIndexes, nodesType, 1 )
        Fieldml_SetParameterDataLocation( fml, nodalIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "1 2 ", 4 )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "2 3 ", 4 )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "3 4 ", 4 )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "4 5 ", 4 )
        
        val elementIndexes = Fieldml_CreateParametersEvaluator( fml, "test.element_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, elementIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, elementIndexes, elementsType, 1 )
        Fieldml_SetParameterDataLocation( fml, elementIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, elementIndexes, "2 6 ", 4 )

        val globalIndexes = Fieldml_CreateParametersEvaluator( fml, "test.global_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, globalIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_SetParameterDataLocation( fml, globalIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, globalIndexes, "1 ", 2 )
        
        val nodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_params", dofParams )
        Fieldml_SetBind( fml, nodalParams, dofIndexType, nodalIndexes )

        val elementParams = Fieldml_CreateReferenceEvaluator( fml, "test.element_params", dofParams )
        Fieldml_SetBind( fml, elementParams, dofIndexType, elementIndexes )

        val globalParams = Fieldml_CreateReferenceEvaluator( fml, "test.global_params", dofParams )
        Fieldml_SetBind( fml, globalParams, dofIndexType, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_nodal_params", nodalParams )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesType, connectivity )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "library.fem.bilinear_lagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "library.parameters.bilinear_lagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "library.xi.2d" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example2.xml" )
    }

    
    private def exportExample3() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_3" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, nodesType, 8 )
        
        val bilinearNodes = Fieldml_GetObjectByName( fml, "library.local_nodes.square.2x2" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "library.ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshType, "library.shape.square" )
        Fieldml_SetContiguousBoundsCount( fml, meshType, 3 )

        val elementsType = Fieldml_GetMeshElementType( fml, meshType )
        val xiType = Fieldml_GetMeshXiType( fml, meshType )

        val connectivity = Fieldml_CreateParametersEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, connectivity, bilinearNodes, 0 )
        Fieldml_AddSemidenseIndexEvaluator( fml, connectivity, elementsType, 0 )
        
        Fieldml_SetParameterDataLocation( fml, connectivity, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, connectivity, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, connectivity, "1 2 5 6\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "2 3 6 7\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "3 4 7 8", 8 );
        
        Fieldml_SetMeshConnectivity( fml, meshType, connectivity, bilinearNodes )

        val dofIndexType = Fieldml_CreateEnsembleType( fml, "test.dof_number", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, dofIndexType, 6 )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "library.real.1d" )

        val dofParams = Fieldml_CreateParametersEvaluator( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, dofParams, dofIndexType, 0 )
        Fieldml_SetParameterDataLocation( fml, dofParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, dofParams, "0.0 0.5 1.0 1.5 2.0 3.0 ", 24 )

        val dofTypeType = Fieldml_CreateEnsembleType( fml, "test.dof_type", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, dofTypeType, 3 )
        
        val dofIndexes = Fieldml_CreateParametersEvaluator( fml, "test.dof_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, dofIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndexEvaluator( fml, dofIndexes, dofTypeType, 1 )
        Fieldml_AddSemidenseIndexEvaluator( fml, dofIndexes, nodesType, 1 )
        Fieldml_AddSemidenseIndexEvaluator( fml, dofIndexes, elementsType, 1 )
        Fieldml_SetParameterDataLocation( fml, dofIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 1 1 2\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 1 2 2\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 1 3 2\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 2 1 3\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 2 2 3\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 2 3 3\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 3 1 4\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 3 2 4\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 3 3 4\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 4 1 5\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 4 2 5\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "1 4 3 5\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 1 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 2 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 3 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 4 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 5 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 6 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 7 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "2 8 2 6\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 1 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 2 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 3 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 4 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 5 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 6 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 7 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 8 1 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 1 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 2 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 3 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 4 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 5 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 6 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 7 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 8 2 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 1 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 2 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 3 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 4 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 5 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 6 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 7 3 1\n", 8 )
        Fieldml_AddParameterInlineData( fml, dofIndexes, "3 8 3 1", 8 )
        
        val dummyConst1 = Fieldml_CreateContinuousType( fml, "1", FML_INVALID_HANDLE )
        val dummyConst2 = Fieldml_CreateContinuousType( fml, "2", FML_INVALID_HANDLE )
        val dummyConst3 = Fieldml_CreateContinuousType( fml, "3", FML_INVALID_HANDLE )
        
        val nodalIndexes = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_indexes", dofIndexes )
        Fieldml_SetBind( fml, nodalIndexes, dofTypeType, dummyConst1 )
        Fieldml_SetBind( fml, nodalIndexes, elementsType, dummyConst1 )

        val elementIndexes = Fieldml_CreateReferenceEvaluator( fml, "test.element_indexes", dofIndexes )
        Fieldml_SetBind( fml, elementIndexes, dofTypeType, dummyConst2 )
        Fieldml_SetBind( fml, elementIndexes, nodesType, dummyConst1 )

        val globalIndexes = Fieldml_CreateReferenceEvaluator( fml, "test.global_indexes", dofIndexes )
        Fieldml_SetBind( fml, globalIndexes, dofTypeType, dummyConst3 )
        Fieldml_SetBind( fml, globalIndexes, nodesType, dummyConst1 )
        Fieldml_SetBind( fml, globalIndexes, elementsType, dummyConst1 )
        
        val nodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_params", dofParams )
        Fieldml_SetBind( fml, nodalParams, dofIndexType, nodalIndexes )

        val elementParams = Fieldml_CreateReferenceEvaluator( fml, "test.element_params", dofParams )
        Fieldml_SetBind( fml, elementParams, dofIndexType, elementIndexes )

        val globalParams = Fieldml_CreateReferenceEvaluator( fml, "test.global_params", dofParams )
        Fieldml_SetBind( fml, globalParams, dofIndexType, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_nodal_params", nodalParams )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesType, connectivity )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "library.fem.bilinear_lagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "library.parameters.bilinear_lagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "library.xi.2d" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example3.xml" )
    }

    
    def main( args: Array[String] ): Unit =
    {
        exportExample1()
        
        exportExample2()
        
        exportExample3()
    }
}