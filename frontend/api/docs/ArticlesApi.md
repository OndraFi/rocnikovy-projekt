# ArticlesApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createArticle**](ArticlesApi.md#createarticleoperation) | **POST** /api/articles | Create article |
| [**deleteArticle**](ArticlesApi.md#deletearticle) | **DELETE** /api/articles/{id} | Delete article |
| [**getArticle**](ArticlesApi.md#getarticle) | **GET** /api/articles/{id} | Get article |
| [**listArticles**](ArticlesApi.md#listarticles) | **GET** /api/articles | List articles |
| [**updateArticle**](ArticlesApi.md#updatearticleoperation) | **PUT** /api/articles/{id} | Update article |



## createArticle

> ArticleDetailResponse createArticle(createArticleRequest)

Create article

Create a new article

### Example

```ts
import {
  Configuration,
  ArticlesApi,
} from '';
import type { CreateArticleOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticlesApi(config);

  const body = {
    // CreateArticleRequest
    createArticleRequest: ...,
  } satisfies CreateArticleOperationRequest;

  try {
    const data = await api.createArticle(body);
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
| **createArticleRequest** | [CreateArticleRequest](CreateArticleRequest.md) |  | |

### Return type

[**ArticleDetailResponse**](ArticleDetailResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Article created |  -  |
| **400** | Validation error |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteArticle

> deleteArticle(id)

Delete article

Delete an article

### Example

```ts
import {
  Configuration,
  ArticlesApi,
} from '';
import type { DeleteArticleRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticlesApi(config);

  const body = {
    // number
    id: 789,
  } satisfies DeleteArticleRequest;

  try {
    const data = await api.deleteArticle(body);
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
| **204** | Article deleted |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getArticle

> ArticleDetailResponse getArticle(id)

Get article

Get an article by ID

### Example

```ts
import {
  Configuration,
  ArticlesApi,
} from '';
import type { GetArticleRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticlesApi(config);

  const body = {
    // number
    id: 789,
  } satisfies GetArticleRequest;

  try {
    const data = await api.getArticle(body);
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

[**ArticleDetailResponse**](ArticleDetailResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Article found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listArticles

> PaginatedArticleResponse listArticles(pageable, categoryIds)

List articles

List articles with pagination and optional category filter

### Example

```ts
import {
  Configuration,
  ArticlesApi,
} from '';
import type { ListArticlesRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticlesApi(config);

  const body = {
    // Pageable
    pageable: ...,
    // Array<number> | Filter by category IDs (can be multiple) (optional)
    categoryIds: ...,
  } satisfies ListArticlesRequest;

  try {
    const data = await api.listArticles(body);
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
| **categoryIds** | `Array<number>` | Filter by category IDs (can be multiple) | [Optional] |

### Return type

[**PaginatedArticleResponse**](PaginatedArticleResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Articles found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateArticle

> ArticleDetailResponse updateArticle(id, updateArticleRequest)

Update article

Update article fields

### Example

```ts
import {
  Configuration,
  ArticlesApi,
} from '';
import type { UpdateArticleOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticlesApi(config);

  const body = {
    // number
    id: 789,
    // UpdateArticleRequest
    updateArticleRequest: ...,
  } satisfies UpdateArticleOperationRequest;

  try {
    const data = await api.updateArticle(body);
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
| **updateArticleRequest** | [UpdateArticleRequest](UpdateArticleRequest.md) |  | |

### Return type

[**ArticleDetailResponse**](ArticleDetailResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Article updated |  -  |
| **400** | Validation error |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

