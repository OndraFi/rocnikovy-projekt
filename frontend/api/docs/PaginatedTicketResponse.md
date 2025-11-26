
# PaginatedTicketResponse


## Properties

Name | Type
------------ | -------------
`tickets` | [Array&lt;TicketResponse&gt;](TicketResponse.md)
`page` | number
`size` | number
`totalElements` | number
`totalPages` | number

## Example

```typescript
import type { PaginatedTicketResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "tickets": null,
  "page": null,
  "size": null,
  "totalElements": null,
  "totalPages": null,
} satisfies PaginatedTicketResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PaginatedTicketResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


