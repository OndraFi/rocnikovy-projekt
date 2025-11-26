
# TicketResponse


## Properties

Name | Type
------------ | -------------
`id` | number
`title` | string
`description` | string
`createdAt` | Date
`updatedAt` | Date
`state` | string
`assignee` | [UserResponse](UserResponse.md)
`author` | [UserResponse](UserResponse.md)
`articleId` | number

## Example

```typescript
import type { TicketResponse } from ''

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "title": null,
  "description": null,
  "createdAt": null,
  "updatedAt": null,
  "state": null,
  "assignee": null,
  "author": null,
  "articleId": null,
} satisfies TicketResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TicketResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


