# ArticleVersionsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getArticleVersion**](ArticleVersionsApi.md#getarticleversion) | **GET** /api/articles/{articleId}/versions/{versionNumber} | Get version |
| [**listArticleVersions**](ArticleVersionsApi.md#listarticleversions) | **GET** /api/articles/{articleId}/versions | List versions |



## getArticleVersion

> ArticleVersionResponse getArticleVersion(articleId, versionNumber)

Get version

Get a specific version of an article with content.

### Example

```ts
import {
  Configuration,
  ArticleVersionsApi,
} from '';
import type { GetArticleVersionRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticleVersionsApi(config);

  const body = {
    // number
    articleId: 789,
    // number
    versionNumber: 56,
  } satisfies GetArticleVersionRequest;

  try {
    const data = await api.getArticleVersion(body);
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
| **articleId** | `number` |  | [Defaults to `undefined`] |
| **versionNumber** | `number` |  | [Defaults to `undefined`] |

### Return type

[**ArticleVersionResponse**](ArticleVersionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Version found |  -  |
| **401** | Unauthorized |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article or version not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listArticleVersions

> PaginatedArticleVersionResponse listArticleVersions(articleId, pageable)

List versions

Get paginated history of versions for an article (without content).

### Example

```ts
import {
  Configuration,
  ArticleVersionsApi,
} from '';
import type { ListArticleVersionsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ArticleVersionsApi(config);

  const body = {
    // number
    articleId: 789,
    // Pageable
    pageable: ...,
  } satisfies ListArticleVersionsRequest;

  try {
    const data = await api.listArticleVersions(body);
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
| **articleId** | `number` |  | [Defaults to `undefined`] |
| **pageable** | [](.md) |  | [Defaults to `undefined`] |

### Return type

[**PaginatedArticleVersionResponse**](PaginatedArticleVersionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Versions found |  -  |
| **401** | Unauthorized |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Article or version not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

