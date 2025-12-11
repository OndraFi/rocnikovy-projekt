# UsersApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**blockUser**](UsersApi.md#blockuser) | **PATCH** /api/users/{identifier}/block | Block user |
| [**listUsers**](UsersApi.md#listusers) | **GET** /api/users | List users |
| [**unblockUser**](UsersApi.md#unblockuser) | **PATCH** /api/users/{identifier}/unblock | Unblock user |



## blockUser

> UserResponse blockUser(identifier)

Block user

Blocks a user, preventing login

### Example

```ts
import {
  Configuration,
  UsersApi,
} from '';
import type { BlockUserRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UsersApi(config);

  const body = {
    // string
    identifier: identifier_example,
  } satisfies BlockUserRequest;

  try {
    const data = await api.blockUser(body);
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
| **identifier** | `string` |  | [Defaults to `undefined`] |

### Return type

[**UserResponse**](UserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | User blocked |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listUsers

> PaginatedUserResponse listUsers(pageable)

List users

List all users with pagination

### Example

```ts
import {
  Configuration,
  UsersApi,
} from '';
import type { ListUsersRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UsersApi(config);

  const body = {
    // Pageable
    pageable: ...,
  } satisfies ListUsersRequest;

  try {
    const data = await api.listUsers(body);
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

[**PaginatedUserResponse**](PaginatedUserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Users found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## unblockUser

> UserResponse unblockUser(identifier)

Unblock user

Reactivates a user account

### Example

```ts
import {
  Configuration,
  UsersApi,
} from '';
import type { UnblockUserRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new UsersApi(config);

  const body = {
    // string
    identifier: identifier_example,
  } satisfies UnblockUserRequest;

  try {
    const data = await api.unblockUser(body);
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
| **identifier** | `string` |  | [Defaults to `undefined`] |

### Return type

[**UserResponse**](UserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | User unblocked |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

