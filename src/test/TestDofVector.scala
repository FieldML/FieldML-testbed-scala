package test

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType._
import fieldml.jni.DataResourceType._
import fieldml.jni.DataSourceType._

object TestDofVector 
{
    private def exportExample1() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_1" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes" )
        Fieldml_SetEnsembleMembersRange( fml, nodesType, 1, 8, 1 )
        
        val nodesVariable = Fieldml_CreateArgumentEvaluator( fml, "test.mesh_nodes.variable", nodesType )
        
        val bilinearNodesVariable = Fieldml_GetObjectByName( fml, "localNodes.2d.square2x2.variable" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh" )
        val xiType = Fieldml_CreateMeshChartType( fml, meshType, "test.mesh.xi" )
        Fieldml_CreateContinuousTypeComponents( fml, xiType, "test.mesh.xi.components", 2 )
        val elementsType = Fieldml_CreateMeshElementsType( fml, meshType, "test.mesh.elements" )
        Fieldml_SetEnsembleMembersRange( fml, meshType, 1, 3, 1 )
        
        val booleanType = Fieldml_CreateBooleanType( fml, "boolean" )
        val boundsEvaluator = Fieldml_CreateExternalEvaluator( fml, "shape.square", booleanType )
        Fieldml_SetMeshShapes( fml, meshType, boundsEvaluator )
        
        val meshVariable = Fieldml_CreateArgumentEvaluator( fml, "test.mesh.variable", meshType )
        val elementsVariable = Fieldml_GetObjectByName( fml, "test.mesh.variable.elements" )
        val xiVariable = Fieldml_GetObjectByName( fml, "test.mesh.variable.xi" )

        val connectivity = Fieldml_CreateParameterEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_DENSE_ARRAY )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, bilinearNodesVariable, FML_INVALID_HANDLE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, elementsVariable, FML_INVALID_HANDLE )
        
        val connectivityResource = Fieldml_CreateInlineDataResource( fml, "test.bilinear_connectivity.resource" )
        val connectivityData = Fieldml_CreateArrayDataSource( fml, "test.bilinear_connectivity.data", connectivityResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, connectivityData, Array( 4, 3 ) )
        Fieldml_SetDataSource( fml, connectivity, connectivityData )

        Fieldml_AddInlineData( fml, connectivityResource, "1 2 5 6\n", 8 );
        Fieldml_AddInlineData( fml, connectivityResource, "2 3 6 7\n", 8 );
        Fieldml_AddInlineData( fml, connectivityResource, "3 4 7 8\n", 8 );
        
        val fieldValue = Fieldml_GetObjectByName( fml, "real.1d" )
        val nodalParams = Fieldml_CreateParameterEvaluator( fml, "test.nodal_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, nodalParams, DESCRIPTION_DOK_ARRAY )
        Fieldml_AddSparseIndexEvaluator( fml, nodalParams, nodesVariable )

        val nodalResource = Fieldml_CreateInlineDataResource( fml, "test.nodal_params.resource" )
        val nodeKeyData = Fieldml_CreateArrayDataSource( fml, "test.nodal_params.keys.data", nodalResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, nodeKeyData, Array( 1, 4 ) )
        Fieldml_SetKeyDataSource( fml, nodalParams, nodeKeyData )
        Fieldml_AddInlineData( fml, nodalResource, "1 2 3 4\n", 8 ) 
        
        val nodalData = Fieldml_CreateArrayDataSource( fml, "test.nodal_params.values.data", nodalResource, "2", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, nodeKeyData, Array( 1, 4 ) )
        Fieldml_SetDataSource( fml, nodalParams, nodalData )
        Fieldml_AddInlineData( fml, nodalResource, "0.0 0.5 1.0 1.5\n", 16 ) 
        
        val elementParams = Fieldml_CreateParameterEvaluator( fml, "test.element_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, elementParams, DESCRIPTION_DOK_ARRAY )
        Fieldml_AddSparseIndexEvaluator( fml, elementParams, elementsVariable )

        val elementResource = Fieldml_CreateInlineDataResource( fml, "test.element_params.resource" )
        val elementKeys = Fieldml_CreateArrayDataSource( fml, "test.element_params.keys.data", elementResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, elementKeys, Array( 1, 1 ) )
        Fieldml_SetKeyDataSource( fml, elementParams, elementKeys )
        Fieldml_AddInlineData( fml, elementKeys, "2\n", 2 )

        val elementData = Fieldml_CreateArrayDataSource( fml, "test.element_params.values.data", elementResource, "2", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, elementData, Array( 1, 1 ) )
        Fieldml_SetDataSource( fml, elementParams, elementData )
        Fieldml_AddInlineData( fml, elementData, "2.0\n", 4 )

        val globalParams = Fieldml_CreateParameterEvaluator( fml, "test.global_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, globalParams, DESCRIPTION_DENSE_ARRAY )

        val globalResource = Fieldml_CreateInlineDataResource( fml, "test.global_params.resource" )
        val globalData = Fieldml_CreateArrayDataSource( fml, "test.global_params.data", globalResource, "1", 1 )
        Fieldml_SetArrayDataSourceRawSizes( fml, globalData, Array( 1 ) )
        Fieldml_SetDataSource( fml, globalParams, globalData )
        Fieldml_AddInlineData( fml, globalData, "3.0\n", 4 )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "interpolator.2d.unit.bilinearLagrange" )
        val bilinearEnsembleVariable = Fieldml_GetObjectByName( fml, "interpolator.2d.unit.bilinearLagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "parameters.2d.bilinearLagrange" )
        val bilinearParametersVariable = Fieldml_GetObjectByName( fml, "parameters.2d.bilinearLagrange.variable" )
        val generic2d = Fieldml_GetObjectByName( fml, "xi.2d.variable" )
        
        val bilinearNodalParams = Fieldml_CreateAggregateEvaluator( fml, "test.bilinear_nodal_params", bilinearParameters )
        Fieldml_SetIndexEvaluator( fml, bilinearNodalParams, 1, bilinearNodesVariable )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesVariable, connectivity )
        Fieldml_SetDefaultEvaluator( fml, bilinearNodalParams, nodalParams )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiVariable )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParametersVariable, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsVariable )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example1.xml" )
    }

    
    private def exportExample2() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_2" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes" )
        Fieldml_SetEnsembleMembersRange( fml, nodesType, 1, 8, 1 )
        
        val bilinearNodesVariable = Fieldml_GetObjectByName( fml, "localNodes.2d.square2x2.variable" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh" )
        val xiType = Fieldml_CreateMeshChartType( fml, meshType, "test.mesh.xi" )
        Fieldml_CreateContinuousTypeComponents( fml, xiType, "test.mesh.xi.components", 2 )
        val elementsType = Fieldml_CreateMeshElementsType( fml, meshType, "test.mesh.elements" )
        Fieldml_SetEnsembleMembersRange( fml, meshType, 1, 3, 1 )

        val booleanType = Fieldml_CreateBooleanType( fml, "boolean" )
        val boundsEvaluator = Fieldml_CreateExternalEvaluator( fml, "shape.square", booleanType )
        Fieldml_SetMeshShapes( fml, meshType, boundsEvaluator )
        
        val connectivity = Fieldml_CreateParameterEvaluator( fml, "test.bilinear_connectivity", nodesType )
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_DENSE_ARRAY )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, bilinearNodesVariable, FML_INVALID_HANDLE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, elementsType, FML_INVALID_HANDLE )
        
        val connectivityResource = Fieldml_CreateInlineDataResource( fml, "test.bilinear_connectivity.resource" )
        val connectivityData = Fieldml_CreateArrayDataSource( fml, "test.bilinear_connectivity.data", connectivityResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, connectivityData, Array( 4, 3 ) )
        Fieldml_SetDataSource( fml, connectivity, connectivityData )

        Fieldml_AddInlineData( fml, connectivityData, "1 2 5 6\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "2 3 6 7\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "3 4 7 8\n", 8 );
        
        val dofIndexType = Fieldml_CreateEnsembleType( fml, "test.dof_number" )
        Fieldml_SetEnsembleMembersRange( fml, dofIndexType, 1, 6, 1 )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "real.1d" )

        val dofParams = Fieldml_CreateParameterEvaluator( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_DENSE_ARRAY )
        Fieldml_AddDenseIndexEvaluator( fml, dofParams, dofIndexType, FML_INVALID_HANDLE )
        
        val dofResource = Fieldml_CreateInlineDataResource( fml, "test.dof_params.resource" )
        val dofData = Fieldml_CreateArrayDataSource( fml, "test.dof_params.data", dofResource, "1", 1 )
        Fieldml_SetArrayDataSourceRawSizes( fml, dofData, Array( 6 ) )
        Fieldml_SetDataSource( fml, dofParams, dofData )

        Fieldml_AddInlineData( fml, dofData, "0.0 0.5 1.0 1.5 2.0 3.0\n", 24 )
        
        val nodalIndexes = Fieldml_CreateParameterEvaluator( fml, "test.nodal_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, nodalIndexes, DESCRIPTION_DOK_ARRAY )
        Fieldml_AddSparseIndexEvaluator( fml, nodalIndexes, nodesType )
        
        val nodalIndexResource = Fieldml_CreateInlineDataResource( fml, "test.nodal_indexes.resource" )
        val nodalKeyData = Fieldml_CreateArrayDataSource( fml, "test.nodal_indexes.key.data", nodalIndexResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, nodalKeyData, Array( 1, 4 ) )
        Fieldml_AddInlineData( fml, nodalKeyData, "1 2 3 4\n", 8 )
        Fieldml_SetKeyDataSource( fml, nodalIndexes, nodalKeyData )

        val nodalIndexData = Fieldml_CreateArrayDataSource( fml, "test.nodal_indexes.value.data", nodalIndexResource, "2", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, nodalIndexData, Array( 1, 4 ) )
        Fieldml_AddInlineData( fml, nodalIndexData, "2 3 4 5\n", 8 )
        Fieldml_SetDataSource( fml, nodalIndexes, nodalIndexData )
        
        val elementIndexes = Fieldml_CreateParameterEvaluator( fml, "test.element_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, elementIndexes, DESCRIPTION_DOK_ARRAY )
        Fieldml_AddSparseIndexEvaluator( fml, elementIndexes, elementsType )
        
        val elementIndexResource = Fieldml_CreateInlineDataResource( fml, "test.element_indexes.resource" )
        val elementKeyData = Fieldml_CreateArrayDataSource( fml, "test.element_indexes.key.data", elementIndexResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, elementKeyData, Array( 1, 1 ) )
        Fieldml_AddInlineData( fml, elementKeyData, "2\n", 2 )
        Fieldml_SetKeyDataSource( fml, elementIndexes, elementKeyData )

        val elementIndexData = Fieldml_CreateArrayDataSource( fml, "test.element_indexes.value.data", elementIndexResource, "2", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, elementIndexData, Array( 1, 1 ) )
        Fieldml_AddInlineData( fml, elementIndexData, "6\n", 2 )
        Fieldml_SetDataSource( fml, elementIndexes, elementIndexData )

        val globalIndexes = Fieldml_CreateParameterEvaluator( fml, "test.global_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, globalIndexes, DESCRIPTION_DENSE_ARRAY )
        
        val globalIndexResource = Fieldml_CreateInlineDataResource( fml, "test.global_indexes.resource" )
        val globalIndexData = Fieldml_CreateArrayDataSource( fml, "test.global_indexes.data", globalIndexResource, "1", 1 )
        Fieldml_SetArrayDataSourceRawSizes( fml, globalIndexData, Array( 1 ) )
        Fieldml_SetDataSource( fml, globalIndexes, globalIndexData )

        Fieldml_AddInlineData( fml, globalIndexData, "1\n", 2 )
        
        val nodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_params", dofParams )
        Fieldml_SetBind( fml, nodalParams, dofIndexType, nodalIndexes )

        val elementParams = Fieldml_CreateReferenceEvaluator( fml, "test.element_params", dofParams )
        Fieldml_SetBind( fml, elementParams, dofIndexType, elementIndexes )

        val globalParams = Fieldml_CreateReferenceEvaluator( fml, "test.global_params", dofParams )
        Fieldml_SetBind( fml, globalParams, dofIndexType, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_nodal_params", nodalParams )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesType, connectivity )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "interpolator.2d.unit.bilinearLagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "parameters.2d.bilinearLagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "xi.2d.variable" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

//        Fieldml_WriteFile( fml, "test_example2.xml" )
    }

    
    private def exportExample3() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_3" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes" )
        Fieldml_SetEnsembleMembersRange( fml, nodesType, 1, 8, 1 )
        
        val bilinearNodesVariable = Fieldml_GetObjectByName( fml, "localNodes.2d.square2x2.variable" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh" )
        val xiType = Fieldml_CreateMeshChartType( fml, meshType, "test.mesh.xi" )
        Fieldml_CreateContinuousTypeComponents( fml, xiType, "test.mesh.xi.components", 2 )
        val elementsType = Fieldml_CreateMeshElementsType( fml, meshType, "test.mesh.elements" )
        Fieldml_SetEnsembleMembersRange( fml, meshType, 1, 3, 1 )

        val booleanType = Fieldml_CreateBooleanType( fml, "boolean" )
        val boundsEvaluator = Fieldml_CreateExternalEvaluator( fml, "shape.square", booleanType )
        Fieldml_SetMeshShapes( fml, meshType, boundsEvaluator )
        
        val connectivity = Fieldml_CreateParameterEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_DENSE_ARRAY )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, bilinearNodesVariable, FML_INVALID_HANDLE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, elementsType, FML_INVALID_HANDLE )
        
        val connectivityResource = Fieldml_CreateInlineDataResource( fml, "test.bilinear_connectivity.resource" )
        val connectivityData = Fieldml_CreateArrayDataSource( fml, "test.bilinear_connectivity.data", connectivityResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, connectivityData, Array( 4, 3 ) )
        Fieldml_SetDataSource( fml, connectivity, connectivityData )

        Fieldml_AddInlineData( fml, connectivityData, "\n", 1 )
        Fieldml_AddInlineData( fml, connectivityData, "1 2 5 6\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "2 3 6 7\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "3 4 7 8", 8 );

        val dofIndexType = Fieldml_CreateEnsembleType( fml, "test.dof_number" )
        Fieldml_SetEnsembleMembersRange( fml, dofIndexType, 1, 6, 1 )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "real.1d" )

        val dofParams = Fieldml_CreateParameterEvaluator( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_DENSE_ARRAY )
        Fieldml_AddDenseIndexEvaluator( fml, dofParams, dofIndexType, FML_INVALID_HANDLE )
        
        val dofResource = Fieldml_CreateInlineDataResource( fml, "test.dof_params.resource" )
        val dofData = Fieldml_CreateArrayDataSource( fml, "test.dof_params.data", dofResource, "1", 1 )
        Fieldml_SetArrayDataSourceRawSizes( fml, dofData, Array( 6 ) )
        Fieldml_SetDataSource( fml, dofParams, dofData )

        Fieldml_AddInlineData( fml, dofData, "0.0 0.5 1.0 1.5 2.0 3.0\n", 24 )

        val dofTypeType = Fieldml_CreateEnsembleType( fml, "test.dof_type" )
        Fieldml_SetEnsembleMembersRange( fml, dofTypeType, 1, 3, 1 )
        
        val dofIndexes = Fieldml_CreateParameterEvaluator( fml, "test.dof_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, dofIndexes, DESCRIPTION_DOK_ARRAY )
        Fieldml_AddSparseIndexEvaluator( fml, dofIndexes, dofTypeType )
        Fieldml_AddSparseIndexEvaluator( fml, dofIndexes, nodesType )
        Fieldml_AddSparseIndexEvaluator( fml, dofIndexes, elementsType )
        
        val dofIndexResource = Fieldml_CreateInlineDataResource( fml, "test.dof_indexes.resource" )
        val dofIndexKeyData = Fieldml_CreateArrayDataSource( fml, "test.dof_indexes.key.data", dofIndexResource, "1", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, dofIndexKeyData, Array( 3, 44 ) )
        Fieldml_SetDataSource( fml, dofIndexes, dofIndexKeyData )

        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 1 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 1 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 1 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 2 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 2 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 2 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 3 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 3 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 3 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 4 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 4 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "1 4 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 1 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 2 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 3 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 4 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 5 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 6 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 7 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "2 8 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 1 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 2 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 3 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 4 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 5 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 6 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 7 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 8 1\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 1 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 2 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 3 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 4 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 5 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 6 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 7 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 8 2\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 1 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 2 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 3 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 4 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 5 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 6 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 7 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "3 8 3\n", 6 )
        Fieldml_AddInlineData( fml, dofIndexKeyData, "\n", 1 )
        
        val dofIndexData = Fieldml_CreateArrayDataSource( fml, "test.dof_indexes.values.data", dofIndexResource, "48", 2 )
        Fieldml_SetArrayDataSourceRawSizes( fml, dofIndexData, Array( 1, 44 ) )
        Fieldml_SetDataSource( fml, dofIndexes, dofIndexData )

        Fieldml_AddInlineData( fml, dofIndexData, "2\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "2\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "2\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "3\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "3\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "3\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "4\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "4\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "4\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "5\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "5\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "5\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "6\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "1\n", 2 )
        Fieldml_AddInlineData( fml, dofIndexData, "\n", 1 )
        
        val dummyConst1 = Fieldml_CreateContinuousType( fml, "1" )
        val dummyConst2 = Fieldml_CreateContinuousType( fml, "2" )
        val dummyConst3 = Fieldml_CreateContinuousType( fml, "3" )
        
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
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "interpolator.2d.unit.bilinearLagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "parameters.2d.bilinearLagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "xi.2d.variable" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

//        Fieldml_WriteFile( fml, "test_example3.xml" )
    }

    
    def main( args: Array[String] ): Unit =
    {
        exportExample1()
        
        exportExample2()
        
        exportExample3()
    }
}