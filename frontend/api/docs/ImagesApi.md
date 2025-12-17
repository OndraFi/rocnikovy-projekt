# ImagesApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteImage**](ImagesApi.md#deleteimage) | **DELETE** /api/images/{fileName} | Delete image |
| [**getImage**](ImagesApi.md#getimage) | **GET** /api/images/{fileName} | Get image |
| [**listImages**](ImagesApi.md#listimages) | **GET** /api/images | List images |
| [**uploadImage**](ImagesApi.md#uploadimage) | **POST** /api/images | Upload image |



## deleteImage

> deleteImage(fileName)

Delete image

Delete an image

### Example

```ts
import {
  Configuration,
  ImagesApi,
} from '';
import type { DeleteImageRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ImagesApi(config);

  const body = {
    // string
    fileName: fileName_example,
  } satisfies DeleteImageRequest;

  try {
    const data = await api.deleteImage(body);
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
| **fileName** | `string` |  | [Defaults to `undefined`] |

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
| **204** | Image deleted |  -  |
| **404** | Image not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getImage

> string getImage(fileName)

Get image

Download image by ID

### Example

```ts
import {
  Configuration,
  ImagesApi,
} from '';
import type { GetImageRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ImagesApi(config);

  const body = {
    // string
    fileName: fileName_example,
  } satisfies GetImageRequest;

  try {
    const data = await api.getImage(body);
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
| **fileName** | `string` |  | [Defaults to `undefined`] |

### Return type

**string**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `image/jpeg`, `image/png`, `image/gif`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Image found |  -  |
| **404** | Image not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listImages

> PaginatedImageResponse listImages(pageable)

List images

List images with pagination

### Example

```ts
import {
  Configuration,
  ImagesApi,
} from '';
import type { ListImagesRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ImagesApi(config);

  const body = {
    // Pageable
    pageable: ...,
  } satisfies ListImagesRequest;

  try {
    const data = await api.listImages(body);
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

[**PaginatedImageResponse**](PaginatedImageResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Images found |  -  |
| **404** | Image not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## uploadImage

> ImageResponse uploadImage(file)

Upload image

Upload a new image file

### Example

```ts
import {
  Configuration,
  ImagesApi,
} from '';
import type { UploadImageRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new ImagesApi(config);

  const body = {
    // Blob
    file: BINARY_DATA_HERE,
  } satisfies UploadImageRequest;

  try {
    const data = await api.uploadImage(body);
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
| **file** | `Blob` |  | [Defaults to `undefined`] |

### Return type

[**ImageResponse**](ImageResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `multipart/form-data`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Image uploaded successfully |  -  |
| **400** | Invalid file - check size or format |  -  |
| **404** | Image not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

