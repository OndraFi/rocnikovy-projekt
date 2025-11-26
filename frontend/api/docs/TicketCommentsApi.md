# TicketCommentsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createTicketComment**](TicketCommentsApi.md#createticketcommentoperation) | **POST** /api/tickets/{ticketId}/comments | Create comment |
| [**deleteTicketComment**](TicketCommentsApi.md#deleteticketcomment) | **DELETE** /api/tickets/{ticketId}/comments/{commentNumber} | Delete comment |
| [**listTicketComments**](TicketCommentsApi.md#listticketcomments) | **GET** /api/tickets/{ticketId}/comments | List comments |
| [**updateTicketComment**](TicketCommentsApi.md#updateticketcommentoperation) | **PUT** /api/tickets/{ticketId}/comments/{commentNumber} | Update comment |



## createTicketComment

> TicketCommentResponse createTicketComment(ticketId, createTicketCommentRequest)

Create comment

Add a comment to a ticket

### Example

```ts
import {
  Configuration,
  TicketCommentsApi,
} from '';
import type { CreateTicketCommentOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketCommentsApi(config);

  const body = {
    // number
    ticketId: 789,
    // CreateTicketCommentRequest
    createTicketCommentRequest: ...,
  } satisfies CreateTicketCommentOperationRequest;

  try {
    const data = await api.createTicketComment(body);
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
| **ticketId** | `number` |  | [Defaults to `undefined`] |
| **createTicketCommentRequest** | [CreateTicketCommentRequest](CreateTicketCommentRequest.md) |  | |

### Return type

[**TicketCommentResponse**](TicketCommentResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Comment created |  -  |
| **400** | Validation error |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket or comment not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteTicketComment

> deleteTicketComment(ticketId, commentNumber)

Delete comment

Delete a comment

### Example

```ts
import {
  Configuration,
  TicketCommentsApi,
} from '';
import type { DeleteTicketCommentRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketCommentsApi(config);

  const body = {
    // number
    ticketId: 789,
    // number
    commentNumber: 56,
  } satisfies DeleteTicketCommentRequest;

  try {
    const data = await api.deleteTicketComment(body);
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
| **ticketId** | `number` |  | [Defaults to `undefined`] |
| **commentNumber** | `number` |  | [Defaults to `undefined`] |

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
| **204** | Comment deleted |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket or comment not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listTicketComments

> PaginatedTicketCommentResponse listTicketComments(ticketId, pageable)

List comments

Get paginated comments for a ticket

### Example

```ts
import {
  Configuration,
  TicketCommentsApi,
} from '';
import type { ListTicketCommentsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketCommentsApi(config);

  const body = {
    // number
    ticketId: 789,
    // Pageable
    pageable: ...,
  } satisfies ListTicketCommentsRequest;

  try {
    const data = await api.listTicketComments(body);
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
| **ticketId** | `number` |  | [Defaults to `undefined`] |
| **pageable** | [](.md) |  | [Defaults to `undefined`] |

### Return type

[**PaginatedTicketCommentResponse**](PaginatedTicketCommentResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Comments found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket or comment not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateTicketComment

> TicketCommentResponse updateTicketComment(ticketId, commentNumber, updateTicketCommentRequest)

Update comment

Edit a comment

### Example

```ts
import {
  Configuration,
  TicketCommentsApi,
} from '';
import type { UpdateTicketCommentOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketCommentsApi(config);

  const body = {
    // number
    ticketId: 789,
    // number
    commentNumber: 56,
    // UpdateTicketCommentRequest
    updateTicketCommentRequest: ...,
  } satisfies UpdateTicketCommentOperationRequest;

  try {
    const data = await api.updateTicketComment(body);
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
| **ticketId** | `number` |  | [Defaults to `undefined`] |
| **commentNumber** | `number` |  | [Defaults to `undefined`] |
| **updateTicketCommentRequest** | [UpdateTicketCommentRequest](UpdateTicketCommentRequest.md) |  | |

### Return type

[**TicketCommentResponse**](TicketCommentResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Comment updated |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket or comment not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

