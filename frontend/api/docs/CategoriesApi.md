# CategoriesApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createCategory**](CategoriesApi.md#createcategoryoperation) | **POST** /api/categories | Create category |
| [**deleteCategory**](CategoriesApi.md#deletecategory) | **DELETE** /api/categories/{id} | Delete category |
| [**getCategory**](CategoriesApi.md#getcategory) | **GET** /api/categories/{id} | Get category |
| [**listCategories**](CategoriesApi.md#listcategories) | **GET** /api/categories | List categories |
| [**updateCategory**](CategoriesApi.md#updatecategoryoperation) | **PUT** /api/categories/{id} | Update category |



## createCategory

> CategoryResponse createCategory(createCategoryRequest)

Create category

Create a new category.

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '';
import type { CreateCategoryOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // CreateCategoryRequest
    createCategoryRequest: ...,
  } satisfies CreateCategoryOperationRequest;

  try {
    const data = await api.createCategory(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **createCategoryRequest** | [CreateCategoryRequest](CreateCategoryRequest.md) |  | |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Category created |  -  |
| **400** | Validation error |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **409** | Category name already exists |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **404** | Category not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteCategory

> deleteCategory(id)

Delete category

Delete a category

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '';
import type { DeleteCategoryRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // number
    id: 789,
  } satisfies DeleteCategoryRequest;

  try {
    const data = await api.deleteCategory(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `number` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Category deleted |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **404** | Category not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getCategory

> CategoryResponse getCategory(id)

Get category

Get a category by ID

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '';
import type { GetCategoryRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // number
    id: 789,
  } satisfies GetCategoryRequest;

  try {
    const data = await api.getCategory(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `number` |  | [Defaults to `undefined`] |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Category found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **404** | Category not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listCategories

> PaginatedCategoryResponse listCategories(pageable)

List categories

List categories with pagination

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '';
import type { ListCategoriesRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // Pageable
    pageable: ...,
  } satisfies ListCategoriesRequest;

  try {
    const data = await api.listCategories(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pageable** | [](.md) |  | [Defaults to `undefined`] |

### Return type

[**PaginatedCategoryResponse**](PaginatedCategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Categories found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **404** | Category not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateCategory

> CategoryResponse updateCategory(id, updateCategoryRequest)

Update category

Update category fields

### Example

```ts
import {
  Configuration,
  CategoriesApi,
} from '';
import type { UpdateCategoryOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new CategoriesApi(config);

  const body = {
    // number
    id: 789,
    // UpdateCategoryRequest
    updateCategoryRequest: ...,
  } satisfies UpdateCategoryOperationRequest;

  try {
    const data = await api.updateCategory(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `number` |  | [Defaults to `undefined`] |
| **updateCategoryRequest** | [UpdateCategoryRequest](UpdateCategoryRequest.md) |  | |

### Return type

[**CategoryResponse**](CategoryResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Category updated |  -  |
| **400** | Validation error |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **409** | Category name already exists |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **404** | Category not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

