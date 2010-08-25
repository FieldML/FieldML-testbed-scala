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
        
        val nodesDomain = Fieldml_CreateEnsembleDomain( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, nodesDomain, 8 )
        
        val bilinearNodes = Fieldml_GetNamedObject( fml, "library.local_nodes.square.2x2" )
        
        val xiEnsemble = Fieldml_GetNamedObject( fml, "library.ensemble.xi.2d" )
        
        val meshDomain = Fieldml_CreateMeshDomain( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshDomain, "library.shape.square" )
        Fieldml_SetContiguousBoundsCount( fml, meshDomain, 3 )
        
        val elementsDomain = Fieldml_GetMeshElementDomain( fml, meshDomain )
        val xiDomain = Fieldml_GetMeshXiDomain( fml, meshDomain )

        val connectivity = Fieldml_CreateEnsembleParameters( fml, "test.bilinear_connectivity", nodesDomain )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, connectivity, bilinearNodes, 0 )
        Fieldml_AddSemidenseIndex( fml, connectivity, elementsDomain, 0 )
        
        Fieldml_SetParameterDataLocation( fml, connectivity, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, connectivity, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, connectivity, "1 2 5 6\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "2 3 6 7\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "3 4 7 8", 8 );
        
        Fieldml_SetMeshConnectivity( fml, meshDomain, connectivity, bilinearNodes )
        
        val fieldValue = Fieldml_GetNamedObject( fml, "library.real.1d" )
        val nodalParams = Fieldml_CreateContinuousParameters( fml, "test.nodal_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, nodalParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, nodalParams, nodesDomain, 1 )
        Fieldml_SetParameterDataLocation( fml, nodalParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, nodalParams, "1 0.0 ", 6 )
        Fieldml_AddParameterInlineData( fml, nodalParams, "2 0.5 ", 6 )
        Fieldml_AddParameterInlineData( fml, nodalParams, "3 1.0 ", 6 )
        Fieldml_AddParameterInlineData( fml, nodalParams, "4 1.5 ", 6 )
        
        val elementParams = Fieldml_CreateContinuousParameters( fml, "test.element_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, elementParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, elementParams, elementsDomain, 1 )
        Fieldml_SetParameterDataLocation( fml, elementParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, elementParams, "2 2.0 ", 6 )

        val globalParams = Fieldml_CreateContinuousParameters( fml, "test.global_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, globalParams, DESCRIPTION_SEMIDENSE )
        Fieldml_SetParameterDataLocation( fml, globalParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, globalParams, "3.0 ", 4 )
        
        val bilinearNodalParams = Fieldml_CreateContinuousReference( fml, "test.bilinear_nodal_params", nodalParams, fieldValue )
        Fieldml_SetAlias( fml, bilinearNodalParams, nodesDomain, connectivity )
        
        val bilinearEvaluator = Fieldml_GetNamedObject( fml, "library.fem.bilinear_lagrange" )
        val bilinearParameters = Fieldml_GetNamedObject( fml, "library.parameters.bilinear_lagrange" )
        val generic2d = Fieldml_GetNamedObject( fml, "library.xi.2d" )
        
        val bilinearInterpolator = Fieldml_CreateContinuousReference( fml, "test.bilinear_interpolator", bilinearEvaluator, fieldValue )
        Fieldml_SetAlias( fml, bilinearInterpolator, generic2d, xiDomain )
        Fieldml_SetAlias( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreateContinuousPiecewise( fml, "test.field", elementsDomain, fieldValue )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example1.xml" )
    }

    
    private def exportExample2() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_2" )
        
        val nodesDomain = Fieldml_CreateEnsembleDomain( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, nodesDomain, 8 )
        
        val bilinearNodes = Fieldml_GetNamedObject( fml, "library.local_nodes.square.2x2" )
        
        val xiEnsemble = Fieldml_GetNamedObject( fml, "library.ensemble.xi.2d" )
        
        val meshDomain = Fieldml_CreateMeshDomain( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshDomain, "library.shape.square" )
        Fieldml_SetContiguousBoundsCount( fml, meshDomain, 3 )

        val elementsDomain = Fieldml_GetMeshElementDomain( fml, meshDomain )
        val xiDomain = Fieldml_GetMeshXiDomain( fml, meshDomain )

        val connectivity = Fieldml_CreateEnsembleParameters( fml, "test.bilinear_connectivity", nodesDomain )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, connectivity, bilinearNodes, 0 )
        Fieldml_AddSemidenseIndex( fml, connectivity, elementsDomain, 0 )
        
        Fieldml_SetParameterDataLocation( fml, connectivity, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, connectivity, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, connectivity, "1 2 5 6\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "2 3 6 7\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "3 4 7 8", 8 );
        
        Fieldml_SetMeshConnectivity( fml, meshDomain, connectivity, bilinearNodes )

        val dofIndexDomain = Fieldml_CreateEnsembleDomain( fml, "test.dof_number", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, dofIndexDomain, 6 )
        
        val fieldValue = Fieldml_GetNamedObject( fml, "library.real.1d" )

        val dofParams = Fieldml_CreateContinuousParameters( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, dofParams, dofIndexDomain, 0 )
        Fieldml_SetParameterDataLocation( fml, dofParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, dofParams, "0.0 0.5 1.0 1.5 2.0 3.0 ", 24 )
        
        val nodalIndexes = Fieldml_CreateEnsembleParameters( fml, "test.nodal_indexes", dofIndexDomain )
        Fieldml_SetParameterDataDescription( fml, nodalIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, nodalIndexes, nodesDomain, 1 )
        Fieldml_SetParameterDataLocation( fml, nodalIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "1 2 ", 4 )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "2 3 ", 4 )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "3 4 ", 4 )
        Fieldml_AddParameterInlineData( fml, nodalIndexes, "4 5 ", 4 )
        
        val elementIndexes = Fieldml_CreateEnsembleParameters( fml, "test.element_indexes", dofIndexDomain )
        Fieldml_SetParameterDataDescription( fml, elementIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, elementIndexes, elementsDomain, 1 )
        Fieldml_SetParameterDataLocation( fml, elementIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, elementIndexes, "2 6 ", 4 )

        val globalIndexes = Fieldml_CreateEnsembleParameters( fml, "test.global_indexes", dofIndexDomain )
        Fieldml_SetParameterDataDescription( fml, globalIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_SetParameterDataLocation( fml, globalIndexes, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, globalIndexes, "1 ", 2 )
        
        val nodalParams = Fieldml_CreateContinuousReference( fml, "test.nodal_params", dofParams, fieldValue )
        Fieldml_SetAlias( fml, nodalParams, dofIndexDomain, nodalIndexes )

        val elementParams = Fieldml_CreateContinuousReference( fml, "test.element_params", dofParams, fieldValue )
        Fieldml_SetAlias( fml, elementParams, dofIndexDomain, elementIndexes )

        val globalParams = Fieldml_CreateContinuousReference( fml, "test.global_params", dofParams, fieldValue )
        Fieldml_SetAlias( fml, globalParams, dofIndexDomain, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateContinuousReference( fml, "test.bilinear_nodal_params", nodalParams, fieldValue )
        Fieldml_SetAlias( fml, bilinearNodalParams, nodesDomain, connectivity )
        
        val bilinearEvaluator = Fieldml_GetNamedObject( fml, "library.fem.bilinear_lagrange" )
        val bilinearParameters = Fieldml_GetNamedObject( fml, "library.parameters.bilinear_lagrange" )
        val generic2d = Fieldml_GetNamedObject( fml, "library.xi.2d" )
        
        val bilinearInterpolator = Fieldml_CreateContinuousReference( fml, "test.bilinear_interpolator", bilinearEvaluator, fieldValue )
        Fieldml_SetAlias( fml, bilinearInterpolator, generic2d, xiDomain )
        Fieldml_SetAlias( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreateContinuousPiecewise( fml, "test.field", elementsDomain, fieldValue )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example2.xml" )
    }

    
    private def exportExample3() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_3" )
        
        val nodesDomain = Fieldml_CreateEnsembleDomain( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, nodesDomain, 8 )
        
        val bilinearNodes = Fieldml_GetNamedObject( fml, "library.local_nodes.square.2x2" )
        
        val xiEnsemble = Fieldml_GetNamedObject( fml, "library.ensemble.xi.2d" )
        
        val meshDomain = Fieldml_CreateMeshDomain( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshDomain, "library.shape.square" )
        Fieldml_SetContiguousBoundsCount( fml, meshDomain, 3 )

        val elementsDomain = Fieldml_GetMeshElementDomain( fml, meshDomain )
        val xiDomain = Fieldml_GetMeshXiDomain( fml, meshDomain )

        val connectivity = Fieldml_CreateEnsembleParameters( fml, "test.bilinear_connectivity", nodesDomain )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, connectivity, bilinearNodes, 0 )
        Fieldml_AddSemidenseIndex( fml, connectivity, elementsDomain, 0 )
        
        Fieldml_SetParameterDataLocation( fml, connectivity, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, connectivity, "\n", 1 )
        Fieldml_AddParameterInlineData( fml, connectivity, "1 2 5 6\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "2 3 6 7\n", 8 );
        Fieldml_AddParameterInlineData( fml, connectivity, "3 4 7 8", 8 );
        
        Fieldml_SetMeshConnectivity( fml, meshDomain, connectivity, bilinearNodes )

        val dofIndexDomain = Fieldml_CreateEnsembleDomain( fml, "test.dof_number", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, dofIndexDomain, 6 )
        
        val fieldValue = Fieldml_GetNamedObject( fml, "library.real.1d" )

        val dofParams = Fieldml_CreateContinuousParameters( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, dofParams, dofIndexDomain, 0 )
        Fieldml_SetParameterDataLocation( fml, dofParams, LOCATION_INLINE )
        Fieldml_AddParameterInlineData( fml, dofParams, "0.0 0.5 1.0 1.5 2.0 3.0 ", 24 )

        val dofTypeDomain = Fieldml_CreateEnsembleDomain( fml, "test.dof_type", FML_INVALID_HANDLE )
        Fieldml_SetContiguousBoundsCount( fml, dofTypeDomain, 3 )
        
        val dofIndexes = Fieldml_CreateEnsembleParameters( fml, "test.dof_indexes", dofIndexDomain )
        Fieldml_SetParameterDataDescription( fml, dofIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSemidenseIndex( fml, dofIndexes, dofTypeDomain, 1 )
        Fieldml_AddSemidenseIndex( fml, dofIndexes, nodesDomain, 1 )
        Fieldml_AddSemidenseIndex( fml, dofIndexes, elementsDomain, 1 )
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
        
        val dummyConst1 = Fieldml_CreateContinuousDomain( fml, "1", FML_INVALID_HANDLE )
        val dummyConst2 = Fieldml_CreateContinuousDomain( fml, "2", FML_INVALID_HANDLE )
        val dummyConst3 = Fieldml_CreateContinuousDomain( fml, "3", FML_INVALID_HANDLE )
        
        val nodalIndexes = Fieldml_CreateContinuousReference( fml, "test.nodal_indexes", dofIndexes, dofIndexDomain )
        Fieldml_SetAlias( fml, nodalIndexes, dofTypeDomain, dummyConst1 )
        Fieldml_SetAlias( fml, nodalIndexes, elementsDomain, dummyConst1 )

        val elementIndexes = Fieldml_CreateContinuousReference( fml, "test.element_indexes", dofIndexes, dofIndexDomain )
        Fieldml_SetAlias( fml, elementIndexes, dofTypeDomain, dummyConst2 )
        Fieldml_SetAlias( fml, elementIndexes, nodesDomain, dummyConst1 )

        val globalIndexes = Fieldml_CreateContinuousReference( fml, "test.global_indexes", dofIndexes, dofIndexDomain )
        Fieldml_SetAlias( fml, globalIndexes, dofTypeDomain, dummyConst3 )
        Fieldml_SetAlias( fml, globalIndexes, nodesDomain, dummyConst1 )
        Fieldml_SetAlias( fml, globalIndexes, elementsDomain, dummyConst1 )
        
        val nodalParams = Fieldml_CreateContinuousReference( fml, "test.nodal_params", dofParams, fieldValue )
        Fieldml_SetAlias( fml, nodalParams, dofIndexDomain, nodalIndexes )

        val elementParams = Fieldml_CreateContinuousReference( fml, "test.element_params", dofParams, fieldValue )
        Fieldml_SetAlias( fml, elementParams, dofIndexDomain, elementIndexes )

        val globalParams = Fieldml_CreateContinuousReference( fml, "test.global_params", dofParams, fieldValue )
        Fieldml_SetAlias( fml, globalParams, dofIndexDomain, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateContinuousReference( fml, "test.bilinear_nodal_params", nodalParams, fieldValue )
        Fieldml_SetAlias( fml, bilinearNodalParams, nodesDomain, connectivity )
        
        val bilinearEvaluator = Fieldml_GetNamedObject( fml, "library.fem.bilinear_lagrange" )
        val bilinearParameters = Fieldml_GetNamedObject( fml, "library.parameters.bilinear_lagrange" )
        val generic2d = Fieldml_GetNamedObject( fml, "library.xi.2d" )
        
        val bilinearInterpolator = Fieldml_CreateContinuousReference( fml, "test.bilinear_interpolator", bilinearEvaluator, fieldValue )
        Fieldml_SetAlias( fml, bilinearInterpolator, generic2d, xiDomain )
        Fieldml_SetAlias( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreateContinuousPiecewise( fml, "test.field", elementsDomain, fieldValue )
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