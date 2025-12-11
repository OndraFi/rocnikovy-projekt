# TicketsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**changeTicketState**](TicketsApi.md#changeticketstate) | **PATCH** /api/tickets/{ticketId}/state | Transition ticket state |
| [**createTicket**](TicketsApi.md#createticketoperation) | **POST** /api/tickets | Create ticket |
| [**deleteTicket**](TicketsApi.md#deleteticket) | **DELETE** /api/tickets/{ticketId} | Delete ticket |
| [**getTicket**](TicketsApi.md#getticket) | **GET** /api/tickets/{ticketId} | Get ticket |
| [**listMyTickets**](TicketsApi.md#listmytickets) | **GET** /api/tickets/my | List my tickets |
| [**listTickets**](TicketsApi.md#listtickets) | **GET** /api/tickets | List tickets |
| [**updateTicket**](TicketsApi.md#updateticketoperation) | **PUT** /api/tickets/{ticketId} | Update ticket |



## changeTicketState

> TicketResponse changeTicketState(ticketId, transitionTicketRequest)

Transition ticket state

Change the state of a ticket

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { ChangeTicketStateRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // number
    ticketId: 789,
    // TransitionTicketRequest
    transitionTicketRequest: ...,
  } satisfies ChangeTicketStateRequest;

  try {
    const data = await api.changeTicketState(body);
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
| **transitionTicketRequest** | [TransitionTicketRequest](TransitionTicketRequest.md) |  | |

### Return type

[**TicketResponse**](TicketResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Ticket state transitioned |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **422** | Invalid state transition |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## createTicket

> TicketResponse createTicket(createTicketRequest)

Create ticket

Create a new ticket

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { CreateTicketOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // CreateTicketRequest
    createTicketRequest: ...,
  } satisfies CreateTicketOperationRequest;

  try {
    const data = await api.createTicket(body);
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
| **createTicketRequest** | [CreateTicketRequest](CreateTicketRequest.md) |  | |

### Return type

[**TicketResponse**](TicketResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Ticket created |  -  |
| **400** | Validation error |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteTicket

> deleteTicket(ticketId)

Delete ticket

Delete a ticket

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { DeleteTicketRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // number
    ticketId: 789,
  } satisfies DeleteTicketRequest;

  try {
    const data = await api.deleteTicket(body);
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
| **204** | Ticket deleted |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getTicket

> TicketResponse getTicket(ticketId)

Get ticket

Get a ticket by ID

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { GetTicketRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // number
    ticketId: 789,
  } satisfies GetTicketRequest;

  try {
    const data = await api.getTicket(body);
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

### Return type

[**TicketResponse**](TicketResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Ticket found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listMyTickets

> PaginatedTicketResponse listMyTickets(pageable, filterType)

List my tickets

List tickets assigned to me or owned by me, filterable by type

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { ListMyTicketsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // Pageable
    pageable: ...,
    // 'ASSIGNED' | 'OWNED' | 'ALL' (optional)
    filterType: filterType_example,
  } satisfies ListMyTicketsRequest;

  try {
    const data = await api.listMyTickets(body);
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
| **filterType** | `ASSIGNED`, `OWNED`, `ALL` |  | [Optional] [Defaults to `&#39;ALL&#39;`] [Enum: ASSIGNED, OWNED, ALL] |

### Return type

[**PaginatedTicketResponse**](PaginatedTicketResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tickets found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listTickets

> PaginatedTicketResponse listTickets(pageable)

List tickets

List tickets with pagination

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { ListTicketsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // Pageable
    pageable: ...,
  } satisfies ListTicketsRequest;

  try {
    const data = await api.listTickets(body);
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

[**PaginatedTicketResponse**](PaginatedTicketResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Tickets found |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateTicket

> TicketResponse updateTicket(ticketId, updateTicketRequest)

Update ticket

Update ticket fields

### Example

```ts
import {
  Configuration,
  TicketsApi,
} from '';
import type { UpdateTicketOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new TicketsApi(config);

  const body = {
    // number
    ticketId: 789,
    // UpdateTicketRequest
    updateTicketRequest: ...,
  } satisfies UpdateTicketOperationRequest;

  try {
    const data = await api.updateTicket(body);
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
| **updateTicketRequest** | [UpdateTicketRequest](UpdateTicketRequest.md) |  | |

### Return type

[**TicketResponse**](TicketResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Ticket updated |  -  |
| **400** | Validation error |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **403** | Not allowed - insufficient role |  -  |
| **404** | Ticket not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

