# AuthenticationApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**changePassword**](AuthenticationApi.md#changepassword) | **POST** /api/auth/change-password | Change password |
| [**confirmPasswordReset**](AuthenticationApi.md#confirmpasswordreset) | **POST** /api/auth/reset-password | Confirm password reset |
| [**login**](AuthenticationApi.md#loginoperation) | **POST** /api/auth/login | Login |
| [**register**](AuthenticationApi.md#registeroperation) | **POST** /api/auth/register | Register a new user |
| [**requestPasswordReset**](AuthenticationApi.md#requestpasswordreset) | **POST** /api/auth/request-password-reset | Request password reset |



## changePassword

> MessageResponse changePassword(passwordChangeRequest)

Change password

Change password for the authenticated user

### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '';
import type { ChangePasswordRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthenticationApi(config);

  const body = {
    // PasswordChangeRequest
    passwordChangeRequest: ...,
  } satisfies ChangePasswordRequest;

  try {
    const data = await api.changePassword(body);
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
| **passwordChangeRequest** | [PasswordChangeRequest](PasswordChangeRequest.md) |  | |

### Return type

[**MessageResponse**](MessageResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Password changed |  -  |
| **401** | Unauthorized - invalid or expired token |  -  |
| **422** | Old password incorrect |  -  |
| **400** | Validation error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## confirmPasswordReset

> MessageResponse confirmPasswordReset(passwordResetConfirmRequest)

Confirm password reset

Reset password using the provided reset code

### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '';
import type { ConfirmPasswordResetRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new AuthenticationApi();

  const body = {
    // PasswordResetConfirmRequest
    passwordResetConfirmRequest: ...,
  } satisfies ConfirmPasswordResetRequest;

  try {
    const data = await api.confirmPasswordReset(body);
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
| **passwordResetConfirmRequest** | [PasswordResetConfirmRequest](PasswordResetConfirmRequest.md) |  | |

### Return type

[**MessageResponse**](MessageResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Password reset successful |  -  |
| **422** | Invalid/expired reset code |  -  |
| **400** | Validation error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## login

> LoginResponse login(loginRequest)

Login

Authenticate a user and return a JWT

### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '';
import type { LoginOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new AuthenticationApi();

  const body = {
    // LoginRequest
    loginRequest: ...,
  } satisfies LoginOperationRequest;

  try {
    const data = await api.login(body);
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
| **loginRequest** | [LoginRequest](LoginRequest.md) |  | |

### Return type

[**LoginResponse**](LoginResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Login successful |  -  |
| **401** | Invalid credentials |  -  |
| **400** | Validation error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## register

> UserResponse register(registerRequest)

Register a new user

Create a new user with unique username and email

### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '';
import type { RegisterOperationRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new AuthenticationApi();

  const body = {
    // RegisterRequest
    registerRequest: ...,
  } satisfies RegisterOperationRequest;

  try {
    const data = await api.register(body);
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
| **registerRequest** | [RegisterRequest](RegisterRequest.md) |  | |

### Return type

[**UserResponse**](UserResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Registration successful |  -  |
| **409** | Username/email already taken |  -  |
| **400** | Validation error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## requestPasswordReset

> PasswordResetCodeResponse requestPasswordReset(passwordResetRequest)

Request password reset

Generate a password reset code for the given identifier (username/email)

### Example

```ts
import {
  Configuration,
  AuthenticationApi,
} from '';
import type { RequestPasswordResetRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new AuthenticationApi();

  const body = {
    // PasswordResetRequest
    passwordResetRequest: ...,
  } satisfies RequestPasswordResetRequest;

  try {
    const data = await api.requestPasswordReset(body);
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
| **passwordResetRequest** | [PasswordResetRequest](PasswordResetRequest.md) |  | |

### Return type

[**PasswordResetCodeResponse**](PasswordResetCodeResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Reset code generated |  -  |
| **404** | User not found |  -  |
| **400** | Validation error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

