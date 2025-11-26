
# TicketCommentResponse


## Properties

Name | Type
------------ | -------------
`number` | number
`content` | string
`createdAt` | Date
`updatedAt` | Date
`author` | [UserResponse](UserResponse.md)

## Example

```typescript
import type { TicketCommentResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "number": null,
  "content": null,
  "createdAt": null,
  "updatedAt": null,
  "author": null,
} satisfies TicketCommentResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TicketCommentResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


